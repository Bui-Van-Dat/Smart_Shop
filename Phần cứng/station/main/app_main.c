#include <string.h>
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "esp_system.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_log.h"
#include "nvs_flash.h"
#include "freertos/semphr.h"
#include "freertos/queue.h"

#include "lwip/sockets.h"
#include "lwip/dns.h"
#include "lwip/netdb.h"

#include "lwip/err.h"
#include "lwip/sys.h"
#include "http_server_app.h"
#include "output_iot.h"
#include "bme280.h"
#include "i2c.h"
#include "cJSON.h"
#include <stdio.h>
#include "driver/ledc.h"

#define EXAMPLE_ESP_WIFI_SSID "datduocday"
#define EXAMPLE_ESP_WIFI_PASS "12345679"
#define EXAMPLE_ESP_MAXIMUM_RETRY 5

#define WEB_SERVER "api.thingspeak.com"
#define WEB_PORT "80"

char REQUEST[512];
char GETREQUEST[512];
char SUBREQUEST[100];
char recv_buf[1024];
char *str_data;
double hu, te;

char LIGHT1;
char LIGHT2;
char LIGHT3;
char LIGHT4;
int DOOR;
/* FreeRTOS event group to signal when we are connected*/
static EventGroupHandle_t s_wifi_event_group;

char jsonReadTempHum[120];

/* The event group allows multiple bits for each event, but we only care about two events:
 * - we are connected to the AP with an IP
 * - we failed to connect after the maximum amount of retries */
#define WIFI_CONNECTED_BIT BIT0
#define WIFI_FAIL_BIT BIT1

static const char *TAG = "wifi station";

static int s_retry_num = 0;

// thingspeak
const struct addrinfo hints = {
    .ai_family = AF_INET,
    .ai_socktype = SOCK_STREAM,
};
struct addrinfo *res;
struct in_addr *addr;
int s, r;

static void event_handler(void *arg, esp_event_base_t event_base,
                          int32_t event_id, void *event_data)
{
    if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_START)
    {
        esp_wifi_connect();
    }
    else if (event_base == WIFI_EVENT && event_id == WIFI_EVENT_STA_DISCONNECTED)
    {
        if (s_retry_num < EXAMPLE_ESP_MAXIMUM_RETRY)
        {
            esp_wifi_connect();
            s_retry_num++;
            ESP_LOGI(TAG, "retry to connect to the AP");
        }
        else
        {
            xEventGroupSetBits(s_wifi_event_group, WIFI_FAIL_BIT);
        }
        ESP_LOGI(TAG, "connect to the AP fail");
    }
    else if (event_base == IP_EVENT && event_id == IP_EVENT_STA_GOT_IP)
    {
        ip_event_got_ip_t *event = (ip_event_got_ip_t *)event_data;
        ESP_LOGI(TAG, "got ip:" IPSTR, IP2STR(&event->ip_info.ip));
        s_retry_num = 0;
        xEventGroupSetBits(s_wifi_event_group, WIFI_CONNECTED_BIT);
    }
}

void wifi_init_sta(void)
{
    s_wifi_event_group = xEventGroupCreate();

    ESP_ERROR_CHECK(esp_netif_init());

    ESP_ERROR_CHECK(esp_event_loop_create_default());
    esp_netif_create_default_wifi_sta();

    wifi_init_config_t cfg = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&cfg));

    esp_event_handler_instance_t instance_any_id;
    esp_event_handler_instance_t instance_got_ip;
    ESP_ERROR_CHECK(esp_event_handler_instance_register(WIFI_EVENT,
                                                        ESP_EVENT_ANY_ID,
                                                        &event_handler,
                                                        NULL,
                                                        &instance_any_id));
    ESP_ERROR_CHECK(esp_event_handler_instance_register(IP_EVENT,
                                                        IP_EVENT_STA_GOT_IP,
                                                        &event_handler,
                                                        NULL,
                                                        &instance_got_ip));

    wifi_config_t wifi_config = {
        .sta = {
            .ssid = EXAMPLE_ESP_WIFI_SSID,
            .password = EXAMPLE_ESP_WIFI_PASS,
            /* Setting a password implies station will connect to all security modes including WEP/WPA.
             * However these modes are deprecated and not advisable to be used. Incase your Access point
             * doesn't support WPA2, these mode can be enabled by commenting below line */
            .threshold.authmode = WIFI_AUTH_WPA2_PSK,
        },
    };
    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wifi_config));
    ESP_ERROR_CHECK(esp_wifi_start());

    /* Waiting until either the connection is established (WIFI_CONNECTED_BIT) or connection failed for the maximum
     * number of re-tries (WIFI_FAIL_BIT). The bits are set by event_handler() (see above) */
    EventBits_t bits = xEventGroupWaitBits(s_wifi_event_group,
                                           WIFI_CONNECTED_BIT | WIFI_FAIL_BIT,
                                           pdFALSE,
                                           pdFALSE,
                                           portMAX_DELAY);

    /* xEventGroupWaitBits() returns the bits before the call returned, hence we can test which event actually
     * happened. */
    if (bits & WIFI_CONNECTED_BIT)
    {
        ESP_LOGI(TAG, "connected to ap SSID:%s password:%s",
                 EXAMPLE_ESP_WIFI_SSID, EXAMPLE_ESP_WIFI_PASS);
    }
    else if (bits & WIFI_FAIL_BIT)
    {
        ESP_LOGI(TAG, "Failed to connect to SSID:%s, password:%s",
                 EXAMPLE_ESP_WIFI_SSID, EXAMPLE_ESP_WIFI_PASS);
    }
    else
    {
        ESP_LOGE(TAG, "UNEXPECTED EVENT");
    }

    /* The event will not be processed after unregister */
    ESP_ERROR_CHECK(esp_event_handler_instance_unregister(IP_EVENT, IP_EVENT_STA_GOT_IP, instance_got_ip));
    ESP_ERROR_CHECK(esp_event_handler_instance_unregister(WIFI_EVENT, ESP_EVENT_ANY_ID, instance_any_id));
    vEventGroupDelete(s_wifi_event_group);
}

void switch_data_callback(char *data, int len)
{
    if (*data == '1')
    {
        output_io_set_level(GPIO_NUM_2, 1);
        printf("Turn on\n");
    }
    else if (*data == '0')
    {
        output_io_set_level(GPIO_NUM_2, 0);
        printf("Turn off\n");
    }
}

void bme280_data_callback(void)
{
    bme280_response(jsonReadTempHum, strlen(jsonReadTempHum));
}

// BME280 I2C task
void Read_Bme280_Task(void *params)
{
    // BME280 I2C communication structure
    struct bme280_t bme280 = {
        .bus_write = BME280_I2C_bus_write,
        .bus_read = BME280_I2C_bus_read,
        .dev_addr = BME280_I2C_ADDRESS1,
        .delay_msec = BME280_delay_msek};

    s32 com_rslt;
    s32 v_uncomp_pressure_s32;
    s32 v_uncomp_temperature_s32;
    s32 v_uncomp_humidity_s32;

    // Initialize BME280 sensor and set internal parameters
    com_rslt = bme280_init(&bme280);
    printf("com_rslt %d\n", com_rslt);

    com_rslt += bme280_set_oversamp_pressure(BME280_OVERSAMP_16X);
    com_rslt += bme280_set_oversamp_temperature(BME280_OVERSAMP_2X);
    com_rslt += bme280_set_oversamp_humidity(BME280_OVERSAMP_1X);

    com_rslt += bme280_set_standby_durn(BME280_STANDBY_TIME_1_MS);
    com_rslt += bme280_set_filter(BME280_FILTER_COEFF_16);

    com_rslt += bme280_set_power_mode(BME280_NORMAL_MODE);
    if (com_rslt == SUCCESS)
    {
        while (true)
        {
            vTaskDelay(1000 / portTICK_PERIOD_MS);

            // Read BME280 data
            com_rslt = bme280_read_uncomp_pressure_temperature_humidity(
                &v_uncomp_pressure_s32, &v_uncomp_temperature_s32, &v_uncomp_humidity_s32);

            double temp = bme280_compensate_temperature_double(v_uncomp_temperature_s32);
            char temperature[12];
            sprintf(temperature, "%.2f degC", temp);

            double press = bme280_compensate_pressure_double(v_uncomp_pressure_s32) / 100; // Pa -> hPa
            char pressure[10];
            sprintf(pressure, "%.2f hPa", press);

            double hum = bme280_compensate_humidity_double(v_uncomp_humidity_s32);
            char humidity[10];
            sprintf(humidity, "%.2f %%", hum);

            hu = hum;
            te = temp;

            sprintf(jsonReadTempHum, "{\"temperature\": \"%.1f\",\"humidity\": \"%.1f\"}", temp, hum);
            // Print BME data
            if (com_rslt == SUCCESS)
            {
                printf("Temperature %s\n", temperature);
            }
        }
    }
}

void vTask1(void *pvParameters)
{
    for (;;)
    {
        printf("Task 1\n");
        if (LIGHT1 == '1')
        {
            output_io_set_level(GPIO_NUM_13, 1);
        }
        else if (LIGHT1 == '0')
        {
            output_io_set_level(GPIO_NUM_13, 0);
        }

        vTaskDelay(500 / portTICK_RATE_MS);
    }
}

void vTask2(void *pvParameters)
{
    for (;;)
    {
        printf("Task 2\n");
        if (LIGHT2 == '1')
        {
            output_io_set_level(GPIO_NUM_12, 1);
        }
        else if (LIGHT2 == '0')
        {
            output_io_set_level(GPIO_NUM_12, 0);
        }

        vTaskDelay(500 / portTICK_RATE_MS);
    }
}

void vTask3(void *pvParameters)
{
    for (;;)
    {
        printf("Task 3\n");
        if (LIGHT3 == '1')
        {
            output_io_set_level(GPIO_NUM_2, 1);
        }
        else if (LIGHT3 == '0')
        {
            output_io_set_level(GPIO_NUM_2, 0);
        }

        vTaskDelay(500 / portTICK_RATE_MS);
    }
}
void vTask4(void *pvParameters)
{
    for (;;)
    {
        printf("Task 4\n");
        if (LIGHT4 == '1')
        {
            output_io_set_level(GPIO_NUM_14, 1);
        }
        else if (LIGHT4 == '0')
        {
            output_io_set_level(GPIO_NUM_14, 0);
        }

        vTaskDelay(500 / portTICK_RATE_MS);
    }
}

static char tag[] = "servo1";

int direction = 1; // 1 = up, -1 = down
void sweepServo_task(void *ignore)
{
    for (;;)
    {
        printf("Task 5: %d\n", DOOR);
        int bitSize = 15;
        int minValue = 500;                           // micro seconds (uS)
        int maxValue = 2500;                          // micro seconds (uS)
        int sweepDuration = 1800;                     // milliseconds (ms)
        int duty = (1 << bitSize) * minValue / 20000; // 1638
        int valueChangeRate = 50;                     // msecs

        ESP_LOGD(tag, ">> task_servo1");
        ledc_timer_config_t timer_conf;
        timer_conf.bit_num = LEDC_TIMER_15_BIT;
        timer_conf.freq_hz = 50;
        timer_conf.speed_mode = LEDC_HIGH_SPEED_MODE;
        timer_conf.timer_num = LEDC_TIMER_0;
        ledc_timer_config(&timer_conf);

        ledc_channel_config_t ledc_conf;
        ledc_conf.channel = LEDC_CHANNEL_0;
        ledc_conf.duty = duty;
        ledc_conf.gpio_num = 18;
        ledc_conf.intr_type = LEDC_INTR_DISABLE;
        ledc_conf.speed_mode = LEDC_HIGH_SPEED_MODE;
        ledc_conf.timer_sel = LEDC_TIMER_0;
        ledc_channel_config(&ledc_conf);

        int changesPerSweep = 90;     // 1500/20 -> 75
        // int changesPerSweep = 180;     // 1500/20 -> 75
        int changeDelta = 1600 / changesPerSweep; // 2000/75 -> 26
        // int changeDelta = 2500 / changesPerSweep; // 2000/75 -> 26
        int i;
        ESP_LOGD(tag, "sweepDuration: %d seconds", sweepDuration);
        ESP_LOGD(tag, "changesPerSweep: %d", changesPerSweep);
        ESP_LOGD(tag, "changeDelta: %d", changeDelta);
        ESP_LOGD(tag, "valueChangeRate: %d", valueChangeRate);
        // while (1)
        // {
        direction = DOOR;
        for (i = 0; i < changesPerSweep; i++)
        {
            if (direction > 0)
            {
                duty += changeDelta;
            }
            else
            {
                duty -= changeDelta;
            }

            ledc_set_duty(LEDC_HIGH_SPEED_MODE, LEDC_CHANNEL_0, duty);
            ledc_update_duty(LEDC_HIGH_SPEED_MODE, LEDC_CHANNEL_0);
            vTaskDelay(valueChangeRate / portTICK_PERIOD_MS);
        }

        ESP_LOGD(tag, "Direction now %d", direction);
        // } // End loop forever

        // vTaskDelete(NULL);
    }
}

void app_main(void)
{
    // Initialize NVS
    esp_err_t ret = nvs_flash_init();
    if (ret == ESP_ERR_NVS_NO_FREE_PAGES || ret == ESP_ERR_NVS_NEW_VERSION_FOUND)
    {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ret = nvs_flash_init();
    }
    ESP_ERROR_CHECK(ret);

    // Khởi tạo PWM
    // pwm_init();

    http_set_callback_switch(switch_data_callback);
    http_set_callback_bme280(bme280_data_callback);

    output_io_create(GPIO_NUM_2);
    output_io_create(GPIO_NUM_13);
    output_io_create(GPIO_NUM_12);
    output_io_create(GPIO_NUM_14);
    // output_io_create(GPIO_NUM_18);

    // Initialize I2C parameters
    i2c_master_init();

    // // Read the data from BME280 sensor
    xTaskCreate(Read_Bme280_Task, "Publisher_Task", 1024 * 5, NULL, 8, NULL);
    LIGHT1 = '1';
    LIGHT2 = '1';
    LIGHT3 = '1';
    LIGHT4 = '1';
    DOOR = 0;
    xTaskCreate(vTask1, "vTask1", 1024, NULL, 4, NULL);
    xTaskCreate(vTask2, "vTask2", 1024, NULL, 5, NULL);
    xTaskCreate(vTask3, "vTask3", 1024, NULL, 6, NULL);
    xTaskCreate(vTask4, "vTask4", 1024, NULL, 7, NULL);
    // xTaskCreate(vTask5, "vTask5", 1024, NULL, 8, NULL);
    xTaskCreate(sweepServo_task, "sweepServo_task", 2048, NULL, 5, NULL);
    printf("servo sweep task  started\n");

    ESP_LOGI(TAG, "ESP_WIFI_MODE_STA");
    wifi_init_sta();
    start_webserver();

    // thingspeak: post temp and humi to thingspeak
    for (int i = 0; i < 1; i++)
    {
        int err = getaddrinfo(WEB_SERVER, WEB_PORT, &hints, &res);

        if (err != 0 || res == NULL)
        {
            ESP_LOGE(TAG, "DNS lookup failed err=%d res=%p", err, res);
            vTaskDelay(1000 / portTICK_PERIOD_MS);
            continue;
        }

        /* Code to print the resolved IP.

           Note: inet_ntoa is non-reentrant, look at ipaddr_ntoa_r for "real" code */
        addr = &((struct sockaddr_in *)res->ai_addr)->sin_addr;
        ESP_LOGI(TAG, "DNS lookup succeeded. IP=%s", inet_ntoa(*addr));

        s = socket(res->ai_family, res->ai_socktype, 0);
        if (s < 0)
        {
            ESP_LOGE(TAG, "... Failed to allocate socket.");
            freeaddrinfo(res);
            vTaskDelay(1000 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... allocated socket");

        if (connect(s, res->ai_addr, res->ai_addrlen) != 0)
        {
            ESP_LOGE(TAG, "... socket connect failed errno=%d", errno);
            close(s);
            freeaddrinfo(res);
            vTaskDelay(4000 / portTICK_PERIOD_MS);
            continue;
        }

        ESP_LOGI(TAG, "... connected");
        freeaddrinfo(res);

        sprintf(SUBREQUEST, "api_key=3YLKCNPRL5NY6RQH&field1=%.1f&field2=%.1f", te, hu);
        sprintf(REQUEST, "POST /update HTTP/1.1\nHost: api.thingspeak.com\nConnection: close\nContent-Type: application/x-www-form-urlencoded\nContent-Length:%d\n\n%s\n", strlen(SUBREQUEST), SUBREQUEST);
        if (write(s, REQUEST, strlen(REQUEST)) < 0)
        {
            ESP_LOGE(TAG, "... socket send failed");
            close(s);
            vTaskDelay(4000 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... socket send success");

        struct timeval receiving_timeout;
        receiving_timeout.tv_sec = 5;
        receiving_timeout.tv_usec = 0;
        if (setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, &receiving_timeout,
                       sizeof(receiving_timeout)) < 0)
        {
            ESP_LOGE(TAG, "... failed to set socket receiving timeout");
            close(s);
            vTaskDelay(4000 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... set socket receiving timeout success");

        /* Read HTTP response */
        do
        {
            bzero(recv_buf, sizeof(recv_buf));
            r = read(s, recv_buf, sizeof(recv_buf) - 1);
            for (int i = 0; i < r; i++)
            {
                putchar(recv_buf[i]);
            }
        } while (r > 0);

        ESP_LOGI(TAG, "... done reading from socket. Last read return=%d errno=%d.", r, errno);
        close(s);
        for (int countdown = 0; countdown >= 0; countdown--)
        {
            ESP_LOGI(TAG, "%d... ", countdown);
            vTaskDelay(500 / portTICK_PERIOD_MS);
        }
        ESP_LOGI(TAG, "Starting again!");
    }

    // Get data from thingspeak to control smart service
    while (1)
    {
        int err = getaddrinfo(WEB_SERVER, WEB_PORT, &hints, &res);

        if (err != 0 || res == NULL)
        {
            ESP_LOGE(TAG, "DNS lookup failed err=%d res=%p", err, res);
            vTaskDelay(500 / portTICK_PERIOD_MS);
            continue;
        }

        /* Code to print the resolved IP.

           Note: inet_ntoa is non-reentrant, look at ipaddr_ntoa_r for "real" code */
        addr = &((struct sockaddr_in *)res->ai_addr)->sin_addr;
        ESP_LOGI(TAG, "DNS lookup succeeded. IP=%s", inet_ntoa(*addr));

        s = socket(res->ai_family, res->ai_socktype, 0);
        if (s < 0)
        {
            ESP_LOGE(TAG, "... Failed to allocate socket.");
            freeaddrinfo(res);
            vTaskDelay(500 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... allocated socket");

        if (connect(s, res->ai_addr, res->ai_addrlen) != 0)
        {
            ESP_LOGE(TAG, "... socket connect failed errno=%d", errno);
            close(s);
            freeaddrinfo(res);
            vTaskDelay(2000 / portTICK_PERIOD_MS);
            continue;
        }

        ESP_LOGI(TAG, "... connected");
        freeaddrinfo(res);

        sprintf(GETREQUEST, "GET http://api.thingspeak.com/channels/2187417/feeds.json?api_key=IMDLO4TIQAI6S9MR&results=1\n\n");
        if (write(s, GETREQUEST, strlen(GETREQUEST)) < 0)
        {
            ESP_LOGE(TAG, "... socket send failed");
            close(s);
            vTaskDelay(2000 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... socket send success");

        struct timeval receiving_timeout;
        receiving_timeout.tv_sec = 2;
        receiving_timeout.tv_usec = 0;
        if (setsockopt(s, SOL_SOCKET, SO_RCVTIMEO, &receiving_timeout,
                       sizeof(receiving_timeout)) < 0)
        {
            ESP_LOGE(TAG, "... failed to set socket receiving timeout");
            close(s);
            vTaskDelay(2000 / portTICK_PERIOD_MS);
            continue;
        }
        ESP_LOGI(TAG, "... set socket receiving timeout success");

        /* Read HTTP response */
        do
        {
            bzero(recv_buf, sizeof(recv_buf));
            r = read(s, recv_buf, sizeof(recv_buf) - 1);
            for (int i = 0; i < r; i++)
            {
                putchar(recv_buf[i]);
            }
            str_data = recv_buf;
            cJSON *json = cJSON_Parse(str_data); // Chuyển đổi chuỗi thành đối tượng JSON

            // Truy cập các trường trong đối tượng JSON
            if (json != NULL)
            {
                cJSON *feeds = cJSON_GetObjectItemCaseSensitive(json, "feeds");
                cJSON *feed = cJSON_GetArrayItem(feeds, 0);
                // Light pool
                cJSON *field3_obj = cJSON_GetObjectItemCaseSensitive(feed, "field3");
                if (!cJSON_IsNull(field3_obj))
                {
                    const char *fieldL1 = field3_obj->valuestring;
                    printf("\nfield3: %s\n", fieldL1);
                    if (fieldL1[0] == '0')
                    {
                        LIGHT1 = '0';
                    }
                    else if (fieldL1[0] == '1')
                    {
                        LIGHT1 = '1';
                    }
                }
                else
                {
                    printf("LIGHT1 NOT CHANGE\n");
                }
                // Light living room
                cJSON *field4_obj = cJSON_GetObjectItemCaseSensitive(feed, "field4");
                if (!cJSON_IsNull(field4_obj))
                {
                    const char *fieldL2 = field4_obj->valuestring;
                    printf("\nfield4: %s\n", fieldL2);
                    if (fieldL2[0] == '0')
                    {
                        LIGHT2 = '0';
                    }
                    else if (fieldL2[0] == '1')
                    {
                        LIGHT2 = '1';
                    }
                }
                else
                {
                    printf("LIGHT2 NOT CHANGE\n");
                }
                // Light bedroom
                cJSON *field5_obj = cJSON_GetObjectItemCaseSensitive(feed, "field5");
                if (!cJSON_IsNull(field5_obj))
                {
                    const char *fieldL3 = field5_obj->valuestring;
                    printf("\nfield5: %s\n", fieldL3);
                    if (fieldL3[0] == '0')
                    {
                        LIGHT3 = '0';
                    }
                    else if (fieldL3[0] == '1')
                    {
                        LIGHT3 = '1';
                    }
                }
                else
                {
                    printf("LIGHT3 NOT CHANGE\n");
                }
                // Light bathroom
                cJSON *field6_obj = cJSON_GetObjectItemCaseSensitive(feed, "field6");
                if (!cJSON_IsNull(field6_obj))
                {
                    const char *fieldL4 = field6_obj->valuestring;
                    printf("\nfield6: %s\n", fieldL4);
                    if (fieldL4[0] == '0')
                    {
                        LIGHT4 = '0';
                    }
                    else if (fieldL4[0] == '1')
                    {
                        LIGHT4 = '1';
                    }
                }
                else
                {
                    printf("LIGHT4 NOT CHANGE\n");
                }
                // Light bathroom
                cJSON *field7_obj = cJSON_GetObjectItemCaseSensitive(feed, "field7");
                if (!cJSON_IsNull(field7_obj))
                {
                    const char *fieldD = field7_obj->valuestring;
                    printf("\nfield7: %s\n", fieldD);
                    if (fieldD[0] == '0')
                    {
                        DOOR = -1;
                    }
                    else if (fieldD[0] == '1')
                    {
                        DOOR = 1;
                    }
                }
                else
                {
                    printf("LIGHT4 NOT CHANGE\n");
                }
            }
            else
            {
                printf("JSON parse error\n");
            }
            cJSON_Delete(json);

        } while (r > 0);
        ESP_LOGI(TAG, "... done reading from socket. Last read return=%d errno=%d.", r, errno);
        close(s);
        for (int countdown = 0; countdown >= 0; countdown--)
        {
            ESP_LOGI(TAG, "%d... ", countdown);
            vTaskDelay(1000 / portTICK_PERIOD_MS);
        }
        ESP_LOGI(TAG, "Starting again!");
    }
}
