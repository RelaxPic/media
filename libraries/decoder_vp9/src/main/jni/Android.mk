#
# Copyright (C) 2016 The Android Open Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

WORKING_DIR := $(call my-dir)
# Modified by RelaxPic: optional size optimization; upstream defaults retained.
MEDIA3_SIZE_CFLAGS :=
MEDIA3_SIZE_LDFLAGS :=
ifeq ($(MEDIA3_DECODER_SIZE_OPTIMIZED),true)
MEDIA3_SIZE_CFLAGS := -Oz -flto=thin -ffunction-sections -fdata-sections
MEDIA3_SIZE_LDFLAGS := -flto=thin -Wl,--gc-sections,--icf=safe,--exclude-libs,ALL
endif
include $(CLEAR_VARS)
LIBVPX_ROOT := $(WORKING_DIR)/libvpx

# build libvpx.so
LOCAL_PATH := $(WORKING_DIR)
include libvpx.mk

# build libvpxV2JNI.so
include $(CLEAR_VARS)
LOCAL_PATH := $(WORKING_DIR)
LOCAL_MODULE := libvpxV2JNI
LOCAL_ARM_MODE := arm
LOCAL_CPP_EXTENSION := .cc
LOCAL_SRC_FILES := vpx_jni.cc
LOCAL_LDLIBS := -llog -lz -lm -landroid
LOCAL_SHARED_LIBRARIES := libvpx
LOCAL_STATIC_LIBRARIES := cpufeatures
LOCAL_CFLAGS += -fvisibility=hidden $(MEDIA3_SIZE_CFLAGS)
LOCAL_LDFLAGS += $(MEDIA3_SIZE_LDFLAGS)

include $(BUILD_SHARED_LIBRARY)

$(call import-module,android/cpufeatures)
