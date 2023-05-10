

cmake_minimum_required(VERSION 3.22.1)

project("breakout")

add_library( breakout SHARED native-lib.cpp qrcodegen.cpp qrcodegen.hpp)

find_library(log-lib log)

target_link_libraries(breakout ${log-lib})

target_include_directories(breakout PRIVATE ${CMAKE_SOURCE_DIR}/cpp)



