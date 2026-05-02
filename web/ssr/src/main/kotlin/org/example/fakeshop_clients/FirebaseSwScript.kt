package org.example.fakeshop_clients

fun firebaseSwScript(firebaseConfigJson: String): String = """
importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.2/firebase-messaging-compat.js');

firebase.initializeApp($firebaseConfigJson);
var messaging = firebase.messaging();

self.addEventListener('install', function(event) {
    self.skipWaiting();
});

self.addEventListener('activate', function(event) {
    event.waitUntil(self.clients.claim());
});

// Handles FCM push messages in background (required for Chrome on Android).
// onBackgroundMessage is called by the Firebase Messaging SDK and works across
// all platforms (desktop Chrome, Android Chrome, iOS Safari 16.4+).
messaging.onBackgroundMessage(function(payload) {
    var notification = payload.notification || {};
    var data = payload.data || {};
    return self.registration.showNotification(notification.title || 'FakeShop', {
        body: notification.body || '',
        icon: '/static/img/icon-192.png',
        badge: '/static/img/badge-72.png',
        data: data
    });
});

self.addEventListener('notificationclick', function(event) {
    event.notification.close();

    var data = event.notification.data || {};
    var productId = data.productId;
    var url = productId ? '/en/product/' + productId : '/';

    event.waitUntil(
        clients.matchAll({ type: 'window', includeUncontrolled: true }).then(function(clientList) {
            for (var i = 0; i < clientList.length; i++) {
                var client = clientList[i];
                if (client.url.indexOf(self.location.origin) !== -1 && 'focus' in client) {
                    client.focus();
                    client.navigate(url);
                    return;
                }
            }
            return clients.openWindow(url);
        })
    );
});
""".trimIndent()
