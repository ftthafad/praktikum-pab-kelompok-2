/* ═══════════════════════════════════════════════════════
   Register Page
   Mirrors RegisterScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const RegisterPage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        content.innerHTML = `
            <div class="auth-page">
                <div class="auth-card">
                    <div class="auth-logo">
                        <div class="auth-logo-icon">🌿</div>
                        <h1 class="auth-title">Buat Akun</h1>
                        <p class="auth-subtitle">Daftar dan mulai jelajahi wisata Jawa Tengah</p>
                    </div>

                    <form id="register-form">
                        <div class="form-group">
                            <label class="form-label" for="reg-name">Nama Lengkap</label>
                            <input type="text" class="form-input" id="reg-name" placeholder="Masukkan nama lengkap" required autocomplete="name">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="reg-email">Email</label>
                            <input type="email" class="form-input" id="reg-email" placeholder="nama@email.com" required autocomplete="email">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="reg-password">Password</label>
                            <input type="password" class="form-input" id="reg-password" placeholder="Minimal 8 karakter" required minlength="8" autocomplete="new-password">
                        </div>

                        <div class="form-group">
                            <label class="form-label" for="reg-password-confirm">Konfirmasi Password</label>
                            <input type="password" class="form-input" id="reg-password-confirm" placeholder="Ulangi password" required autocomplete="new-password">
                        </div>

                        <div id="register-error" class="form-error" style="margin-bottom:12px;display:none;"></div>

                        <button type="submit" class="btn btn-primary btn-lg w-full" id="register-submit">
                            <span class="material-icons-round">person_add</span>
                            Daftar
                        </button>
                    </form>

                    <div class="auth-footer">
                        Sudah punya akun? <a href="#/login">Masuk</a>
                    </div>
                </div>
            </div>
        `;

        document.getElementById('register-form').addEventListener('submit', handleRegister);
    }

    async function handleRegister(e) {
        e.preventDefault();
        const name = document.getElementById('reg-name').value.trim();
        const email = document.getElementById('reg-email').value.trim();
        const password = document.getElementById('reg-password').value;
        const confirm = document.getElementById('reg-password-confirm').value;
        const errorEl = document.getElementById('register-error');
        const submitBtn = document.getElementById('register-submit');

        errorEl.style.display = 'none';

        if (password !== confirm) {
            errorEl.textContent = 'Password dan konfirmasi tidak sama';
            errorEl.style.display = 'block';
            return;
        }

        if (password.length < 8) {
            errorEl.textContent = 'Password minimal 8 karakter';
            errorEl.style.display = 'block';
            return;
        }

        submitBtn.disabled = true;
        submitBtn.innerHTML = '<div class="spinner" style="width:20px;height:20px;border-width:2px;"></div> Memproses...';

        try {
            const res = await API.register(name, email, password, confirm);
            Auth.login(res.data);
            Toast.success('Registrasi berhasil! Selamat datang!');
            App.navigate('#/home');
        } catch (err) {
            const msg = err.message || 'Gagal mendaftar. Coba lagi.';
            errorEl.textContent = msg;
            errorEl.style.display = 'block';
            submitBtn.disabled = false;
            submitBtn.innerHTML = '<span class="material-icons-round">person_add</span> Daftar';
        }
    }

    function destroy() {}

    return { render, destroy };
})();
