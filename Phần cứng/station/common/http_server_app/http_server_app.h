#ifndef __HTTP_SERVER_APP_H
#define __HTTP_SERVER_APP_H

typedef void (*http_post_callback_t)(char* data, int len);
typedef void (*http_get_callback_t)(void);

void start_webserver(void);
void stop_webserver(void);
void http_set_callback_switch(void *cb);
void http_set_callback_bme280(void *cb);
void bme280_response(char *data, int len);
void http_set_callback_slider(void *cb);

#endif