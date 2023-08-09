# set(app_src i2c.c)
# set(pri_reg bme280)
# idf_component_register(SRCS ${app_src}
#                     INCLUDE_DIRS "."
#                     REQUIRES ${pri_reg})