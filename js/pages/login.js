/* ═══════════════════════════════════════════════════════
   Login Page
   Mirrors LoginScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const LoginPage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        content.innerHTML = `
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-logo">
                        <div class="auth-logo-icon">🌿</div>
                        <h1 class="auth-title">Selamat Datang</h1>
                        <p class="auth-subtitle">Masuk ke akun TravelWaka kamu</p>
                    </div>

                    <form id="login-form">
                        <div class="form-group">
                            <label class="form-label" for="login-email">Email</label>
                            <input type="email" class="form-input" id="login-email" placeholder="nama@email.com" required autocomplete="email">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="login-password">Password</label>
                            <input type="password" class="form-input" id="login-password" placeholder="Masukkan password" required autocomplete="current-password">
                        </div>

                        <div id="login-error" class="form-error" style="margin-bottom:12px;display:none;"></div>

                        <button type="submit" class="btn btn-primary btn-lg w-full" id="login-submit">
                            <span class="material-icons-round">login</span>
                            Masuk
                        </button>
                    </form>

                    <div class="auth-divider">atau</div>

                    <button class="btn btn-ghost w-full" onclick="App.navigate('#/home')">
                        Lewati, Jelajahi Dulu
                    </button>

                    <div class="auth-footer">
                        Belum punya akun? <a href="#/register">Daftar Sekarang</a>
                    </div>
                </div>
            </div>
        `;

        // Form submit
        document.getElementById('login-form').addEventListener('submit', handleLogin);
    }

    async function handleLogin(e) {
        e.preventDefault();
        const email = document.getElementById('login-email').value.trim();
        const password = document.getElementById('login-password').value;
        const errorEl = document.getElementById('login-error');
        const submitBtn = document.getElementById('login-submit');

        errorEl.style.display = 'none';
        submitBtn.disabled = true;
        submitBtn.innerHTML = '<div class="spinner" style="width:20px;height:20px;border-width:2px;"></div> Memproses...';

        try {
            const res = await API.login(email, password);
            Auth.login(res.data);
            Toast.success('Login berhasil!');

            // Navigate based on role
            const role = Auth.getUserRole();
            if (role === 'super_admin') {
                App.navigate('#/admin');
            } else {
                App.navigate('#/home');
            }
        } catch (err) {
            errorEl.textContent = err.message || 'Email atau password salah';
            errorEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span class="material-icons-round">login</span> Masuk';
        }
    }

    function destroy() {}

    return { render, destroy };
})();
