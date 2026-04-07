// Firebase Cloud Messaging Service Worker
// This handles background push notifications for the web app

self.addEventListener('push', function(event) {
    if (!event.data) return;

    var data;
    try {
        data = event.data.json();
    } catch (e) {
        return;
    }

    var notification = data.notification || {};
    var payload = data.data || {};

    var title = notification.title || 'FakeShop';
    var options = {
        body: notification.body || '',
        icon: '/static/img/icon-192.png',
        badge: '/static/img/badge-72.png',
        data: payload
    };

    event.waitUntil(
        self.registration.showNotification(title, options)
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
