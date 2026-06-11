/* ═══════════════════════════════════════════════════════
   Auth — Token / Session Management
   Mirrors TokenDataStore.kt from Android
   ═══════════════════════════════════════════════════════ */

const Auth = (() => {
    const KEYS = {
        TOKEN: 'tw_token',
        USER_NAME: 'tw_user_name',
        USER_EMAIL: 'tw_user_email',
        USER_ROLE: 'tw_user_role',
        USER_ID: 'tw_user_id',
        ONBOARDING_SEEN: 'tw_onboarding_seen',
    };

    // ── Getters ──────────────────────────────────────
    function getToken() {
        return localStorage.getItem(KEYS.TOKEN) || null;
    }

    function getBearer() {
        const token = getToken();
        return token ? `Bearer ${token}` : '';
    }

    function getUserName() {
        return localStorage.getItem(KEYS.USER_NAME) || '';
    }

    function getUserEmail() {
        return localStorage.getItem(KEYS.USER_EMAIL) || '';
    }

    function getUserRole() {
        return localStorage.getItem(KEYS.USER_ROLE) || 'user';
    }

    function getUserId() {
        return parseInt(localStorage.getItem(KEYS.USER_ID)) || 0;
    }

    function isLoggedIn() {
        const token = getToken();
        return token !== null && token !== '';
    }

    function hasSeenOnboarding() {
        return localStorage.getItem(KEYS.ONBOARDING_SEEN) === 'true';
    }

    // ── Setters ──────────────────────────────────────
    function saveSession(data) {
        if (data.token) localStorage.setItem(KEYS.TOKEN, data.token);
        if (data.user) {
            localStorage.setItem(KEYS.USER_NAME, data.user.name || '');
            localStorage.setItem(KEYS.USER_EMAIL, data.user.email || '');
            localStorage.setItem(KEYS.USER_ROLE, data.user.role || 'user');
            localStorage.setItem(KEYS.USER_ID, data.user.id || 0);
        }
    }

    function setOnboardingSeen() {
        localStorage.setItem(KEYS.ONBOARDING_SEEN, 'true');
    }

    function clearSession() {
        Object.values(KEYS).forEach(key => localStorage.removeItem(key));
    }

    // ── Listeners ────────────────────────────────────
    const listeners = [];

    function onAuthChange(fn) {
        listeners.push(fn);
    }

    function notifyChange() {
        listeners.forEach(fn => fn());
    }

    function login(data) {
        saveSession(data);
        notifyChange();
    }

    function logout() {
        clearSession();
        notifyChange();
    }

    return {
        getToken,
        getBearer,
        getUserName,
        getUserEmail,
        getUserRole,
        getUserId,
        isLoggedIn,
        hasSeenOnboarding,
        saveSession,
        setOnboardingSeen,
        clearSession,
        login,
        logout,
        onAuthChange,
    };
})();
