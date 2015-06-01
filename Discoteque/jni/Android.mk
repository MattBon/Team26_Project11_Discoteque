LOCAL_PATH := $(call my-dir)
#---------------------------------------------------------------

include $(CLEAR_VARS)
LOCAL_MODULE += pdbeatdetection

LOCAL_C_INCLUDES += /Users/MattiaBonomi/Desktop/MMNET/Final_Code/Eclipse_project/pd-0.45-4/src

LOCAL_CFLAGS += -DPD
LOCAL_SRC_FILES += pdbeatdetection.c

LOCAL_LDLIBS += -L/Users/MattiaBonomi/Desktop/MMNET/Final_Code/Eclipse_project/PdCore/libs/armeabi -lpdnative

include $(BUILD_SHARED_LIBRARY)
