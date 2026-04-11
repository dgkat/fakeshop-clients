// Firebase Cloud Messaging Service Worker
// Handles background push notifications for the web app.
// Uses raw Web Push (no Firebase SDK in SW) — FCM delivers standard web push
// payloads that the browser decrypts before handing to the push event.

console.log('[fakeshop-sw] script loaded');

self.addEventListener('install', function(event) {
    console.log('[fakeshop-sw] install');
    self.skipWaiting();
});

self.addEventListener('activate', function(event) {
    console.log('[fakeshop-sw] activate');
    event.waitUntil(self.clients.claim());
});

self.addEventListener('push', function(event) {
    console.log('[fakeshop-sw] push event received', event);

    var title = 'FakeShop';
    var options = {
        body: '',
        icon: '/static/img/icon-192.png',
        badge: '/static/img/badge-72.png',
        data: {}
    };

    if (event.data) {
        try {
            var data = event.data.json();
            console.log('[fakeshop-sw] push payload', data);
            var notification = data.notification || {};
            var payload = data.data || {};
            title = notification.title || title;
            options.body = notification.body || '';
            options.data = payload;
        } catch (e) {
            console.warn('[fakeshop-sw] failed to parse push payload as JSON', e);
            try {
                options.body = event.data.text();
            } catch (e2) {
                console.warn('[fakeshop-sw] failed to read push payload as text', e2);
            }
        }
    } else {
        console.log('[fakeshop-sw] push event had no data — showing placeholder');
        options.body = 'Test push (no payload)';
    }

    event.waitUntil(
        self.registration.showNotification(title, options)
            .then(function() {
                console.log('[fakeshop-sw] showNotification resolved');
            })
            .catch(function(err) {
                console.error('[fakeshop-sw] showNotification rejected', err);
            })
    );
});

self.addEventListener('notificationclick', function(event) {
    console.log('[fakeshop-sw] notificationclick', event.notification.data);
    event.notification.close();

    var data = event.notification.data || {};
    var productId = data.productId;
    var url = '/';

    if (productId) {
        url = '/en/product/' + productId;
    }

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
