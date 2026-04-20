// Firebase Cloud Messaging Service Worker
// Handles background push notifications for the web app.
// Uses raw Web Push (no Firebase SDK in SW) — FCM delivers standard web push
// payloads that the browser decrypts before handing to the push event.

self.addEventListener('install', function(event) {
    self.skipWaiting();
});

self.addEventListener('activate', function(event) {
    event.waitUntil(self.clients.claim());
});

self.addEventListener('push', function(event) {
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
        options.body = 'Test push (no payload)';
    }

    event.waitUntil(
        self.registration.showNotification(title, options)
            .catch(function(err) {
                console.error('[fakeshop-sw] showNotification rejected', err);
            })
    );
});

self.addEventListener('notificationclick', function(event) {
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
