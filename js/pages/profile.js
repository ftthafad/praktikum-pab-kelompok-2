/* ═══════════════════════════════════════════════════════
   Profile Page
   Mirrors ProfileScreen.kt from Android
   ═══════════════════════════════════════════════════════ */

const ProfilePage = (() => {
    async function render() {
        const content = document.getElementById('page-content');

        if (!Auth.isLoggedIn()) {
            App.navigate('#/login');
            return;
        }

        const name = Auth.getUserName();
        const email = Auth.getUserEmail();
        const role = Auth.getUserRole();
        const initial = name ? name.charAt(0).toUpperCase() : '?';

        const roleLabel = {
            'pengelola': 'Pengelola Wisata',
            'super_admin': 'Super Admin',
            'user': 'Wisatawan',
        }[role] || 'Wisatawan';

        content.innerHTML = `
            <div class="page-wrapper page-enter">
                <!-- Header -->
                <div class="profile-header">
                    <div class="profile-avatar">${initial}</div>
                    <div class="profile-name">${esc(name)}</div>
                    <div class="profile-email">${esc(email)}</div>
                    <span class="profile-role-badge">${roleLabel}</span>
                </div>

                <div class="container" style="padding-top:24px; padding-bottom:40px;">
                    <!-- Account Menu -->
                    <div class="profile-menu-group">
                        <div class="profile-menu-label">Akun</div>
                        <div class="profile-menu-card">
                            ${menuItem('person', 'Edit Profil', 'Ubah nama dan foto profil', 'var(--primary)', null)}
                            ${menuItem('notifications', 'Notifikasi', 'Status pengajuan pengelola', 'var(--primary)', null)}
                            ${menuItem('lock', 'Keamanan', 'Ubah password', 'var(--primary)', null)}
                        </div>
                    </div>

                    <!-- Pengelola Menu (only for user role) -->
                    ${role === 'user' ? `
                    <div class="profile-menu-group">
                        <div class="profile-menu-label">Pengelola</div>
                        <div class="profile-menu-card">
                            ${menuItem('store', 'Ajukan Jadi Pengelola', 'Daftarkan destinasi wisatamu', 'var(--primary-medium)', '#/pengajuan')}
                        </div>
                    </div>
                    ` : ''}

                    <!-- Pengelola Wisata Menu (only for pengelola role) -->
                    ${role === 'pengelola' ? `
                    <div class="profile-menu-group">
                        <div class="profile-menu-label">Pengelola Wisata</div>
                        <div class="profile-menu-card">
                            ${menuItem('map', 'Kelola Wisata', 'Tambah, edit, hapus wisata milikmu', 'var(--primary)', '#/pengelola')}
                        </div>
                    </div>
                    ` : ''}

                    <!-- Logout -->
                    <div class="profile-menu-group" style="margin-top:8px;">
                        <div class="profile-menu-card" style="background:rgba(220,38,38,0.05);">
                            <div class="profile-menu-item" id="btn-logout">
                                <div class="profile-menu-icon" style="background:rgba(220,38,38,0.12);">
                                    <span class="material-icons-round" style="color:var(--error);">logout</span>
                                </div>
                                <div class="profile-menu-text">
                                    <h4 style="color:var(--error);">Keluar</h4>
                                    <p>Keluar dari akun</p>
                                </div>
                                <span class="material-icons-round chevron">chevron_right</span>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Logout handler
        document.getElementById('btn-logout').addEventListener('click', () => {
            Modal.show({
                icon: 'logout',
                iconClass: 'modal-icon-danger',
                title: 'Keluar dari Akun?',
                body: 'Kamu akan keluar dari akun ini. Yakin ingin melanjutkan?',
                confirmText: 'Keluar',
                cancelText: 'Batal',
                dangerConfirm: true,
                onConfirm: async () => {
                    try {
                        await API.logout();
                    } catch (e) { /* ignore */ }
                    Auth.logout();
                    Toast.info('Kamu telah keluar');
                    App.navigate('#/login');
                }
            });
        });
    }

    function menuItem(icon, title, subtitle, color, href) {
        const clickAttr = href ? `onclick="App.navigate('${href}')"` : '';
        return `
            <div class="profile-menu-item" ${clickAttr}>
                <div class="profile-menu-icon" style="background:${color}1a;">
                    <span class="material-icons-round" style="color:${color};">${icon}</span>
                </div>
                <div class="profile-menu-text">
                    <h4>${title}</h4>
                    <p>${subtitle}</p>
                </div>
                <span class="material-icons-round chevron">chevron_right</span>
            </div>
        `;
    }

    function destroy() {}

    function esc(str) {
        if (!str) return '';
        const div = document.createElement('div');
        div.textContent = str;
        return div.innerHTML;
    }

    return { render, destroy };
})();
