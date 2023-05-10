#include <jni.h>
#include <string>
#include "qrcodegen.hpp"


extern "C" JNIEXPORT jstring JNICALL
Java_com_example_breakout_MainActivity_stringFromJNI(
        JNIEnv* env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

std::vector<jint> convertToByteVector(const qrcodegen::QrCode& qrCode)
{
    int size = qrCode.getSize();
    std::vector<jint> byteVector;

    for (int y = 0; y < size; ++y)
    {
        for (int x = 0; x < size; ++x)
        {
            int32_t c = qrCode.getModule(x, y) * INT32_MAX;
            byteVector.push_back(c);
        }
    }

    return byteVector;
}

extern "C" JNIEXPORT jintArray JNICALL
Java_com_example_breakout_Breakout_generateQRCode(JNIEnv* env, jobject /* this */)
{
    std::string link = "https://youtu.be/H0BIqsfleJI";
    qrcodegen::QrCode qr = qrcodegen::QrCode::encodeText(link.c_str(), qrcodegen::QrCode::Ecc::MEDIUM);

    std::vector<jint> qrData = convertToByteVector(qr);

    jintArray qrCodeArray = env->NewIntArray(qrData.size());
    env->SetIntArrayRegion(qrCodeArray, 0, qrData.size(), qrData.data());
    jint* encoded_test = (*env).GetIntArrayElements(qrCodeArray, NULL);
    return qrCodeArray;
}

