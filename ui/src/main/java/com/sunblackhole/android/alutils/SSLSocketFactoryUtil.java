/*
 * Copyright © 2020 WireGuard LLC. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package com.sunblackhole.android.alutils;


import android.annotation.SuppressLint;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class SSLSocketFactoryUtil {

    @SuppressLint("TrulyRandom")
    public static SSLSocketFactory createSSLSocketFactory() {

        SSLSocketFactory sSLSocketFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllManager()},
                    new SecureRandom());
            sSLSocketFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return sSLSocketFactory;
    }

    // Install the all-trusting trust manager
//    final SSLContext sslContext = SSLContext.getInstance("SSL");
//    sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
//    // Create an ssl socket factory with our all-trusting manager
//    final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
//
//    OkHttpClient.Builder builder = new OkHttpClient.Builder();
//    builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
//    builder.hostnameVerifier(new HostnameVerifier() {
//        @Override
//        public boolean verify(String hostname, SSLSession session) {
//            return true;
//        }
//    });


    private static class TrustAllManager implements X509TrustManager {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws
                CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws
                CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    public static class TrustAllHostnameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
