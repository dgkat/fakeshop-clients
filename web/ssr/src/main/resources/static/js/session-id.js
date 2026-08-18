/**
 * Browser-side maintenance of the browsing-session cookies.
 *
 * SSR mints (it is the only side that can act before the first document renders, and the only one
 * that works with JS disabled). This file keeps the inactivity clock alive, which the server cannot
 * do: a user who lands on home and then spends 40 minutes in the SPA and search touches SSR exactly
 * once, and a server-only clock would roll them into a new session mid-sitting.
 *
 * The staleness rule below is the same one Kotlin implements in SessionStaleness. Both sides must
 * agree — same window, same comparison, same treatment of a missing timestamp. Changing one without
 * the other is the designed-in failure mode of this hybrid.
 */
(function () {
  var ID_COOKIE = 'fs_session_id';
  var TOUCHED_COOKIE = 'fs_session_touched';
  var WINDOW_MILLIS = 30 * 60 * 1000;
  var COOKIE_MAX_AGE_SECONDS = 60 * 60 * 24;
  var TOUCH_THROTTLE_MILLIS = 60 * 1000;

  var lastTouchWrite = 0;

  function readCookie(name) {
    var prefix = name + '=';
    var parts = document.cookie.split(';');
    for (var i = 0; i < parts.length; i++) {
      var part = parts[i].trim();
      if (part.indexOf(prefix) === 0) {
        var value = part.substring(prefix.length);
        return value ? decodeURIComponent(value) : null;
      }
    }
    return null;
  }

  function writeCookie(name, value) {
    document.cookie =
      name + '=' + encodeURIComponent(value) +
      '; Path=/; Max-Age=' + COOKIE_MAX_AGE_SECONDS + '; SameSite=Lax';
  }

  function newId() {
    if (window.crypto && typeof window.crypto.randomUUID === 'function') {
      return window.crypto.randomUUID();
    }
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
      var r = (Math.random() * 16) | 0;
      return (c === 'x' ? r : (r & 0x3) | 0x8).toString(16);
    });
  }

  // A missing id or a missing/unparseable timestamp is stale — a partially written store must not
  // pin one session forever.
  function isStale(id, touched, now) {
    if (!id) return true;
    if (touched === null || isNaN(touched)) return true;
    return now - touched > WINDOW_MILLIS;
  }

  function current() {
    var now = Date.now();
    var id = readCookie(ID_COOKIE);
    var raw = readCookie(TOUCHED_COOKIE);
    var touched = raw === null ? null : parseInt(raw, 10);

    if (isStale(id, touched, now)) {
      id = newId();
      writeCookie(ID_COOKIE, id);
    }

    writeCookie(TOUCHED_COOKIE, String(now));
    lastTouchWrite = now;
    return id;
  }

  function reset() {
    document.cookie = ID_COOKIE + '=; Path=/; Max-Age=0; SameSite=Lax';
    document.cookie = TOUCHED_COOKIE + '=; Path=/; Max-Age=0; SameSite=Lax';
  }

  function touch() {
    // Every event would rewrite the cookie on every scroll frame; the clock only needs to be
    // accurate to well within the 30-minute window.
    if (Date.now() - lastTouchWrite < TOUCH_THROTTLE_MILLIS) return;
    current();
  }

  current();

  ['click', 'keydown', 'scroll', 'touchstart', 'pointerdown'].forEach(function (event) {
    window.addEventListener(event, touch, { passive: true });
  });

  document.addEventListener('visibilitychange', function () {
    if (document.visibilityState === 'visible') current();
  });

  window.fsSession = { current: current, reset: reset };
})();
