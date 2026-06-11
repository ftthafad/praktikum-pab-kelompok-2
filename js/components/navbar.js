/* ═══════════════════════════════════════════════════════
   Navbar Component
   ═══════════════════════════════════════════════════════ */

const Navbar = (() => {
    function render() {
        const container = document.getElementById('navbar-container');
        const isLoggedIn = Auth.isLoggedIn();
        const role = Auth.getUserRole();
        const name = Auth.getUserName();
        const currentHash = location.hash || '#/home';

        // Determine which nav links to show
        let links = '';

        if (role === 'super_admin' && isLoggedIn) {
            links = `
                <a href="#/admin" class="navbar-link ${currentHash.startsWith('#/admin') ? 'active' : ''}">
                    <span class="material-icons-round">admin_panel_settings</span> Dashboard
                </a>
            `;
        } else {
            links = `
                <a href="#/home" class="navbar-link ${currentHash === '#/home' ? 'active' : ''}">
                    <span class="material-icons-round">home</span> Beranda
                </a>
                <a href="#/explore" class="navbar-link ${currentHash.startsWith('#/explore') ? 'active' : ''}">
                    <span class="material-icons-round">explore</span> Jelajahi
                </a>
                ${isLoggedIn ? `
                <a href="#/bookmark" class="navbar-link ${currentHash === '#/bookmark' ? 'active' : ''}">
                    <span class="material-icons-round">bookmark</span> Tersimpan
                </a>
                ` : ''}
            `;
        }

        // Auth actions
        let actions = '';
        if (isLoggedIn) {
            actions = `
                <a href="#/profile" class="navbar-avatar" title="${name}">
                    ${name ? name.charAt(0).toUpperCase() : '?'}
                </a>
            `;
        } else {
            actions = `
                <a href="#/login" class="btn btn-sm" style="color:rgba(255,255,255,0.8);">Masuk</a>
                <a href="#/register" class="btn btn-sm" style="background:rgba(255,255,255,0.15);color:var(--white);border:1px solid rgba(255,255,255,0.2);">Daftar</a>
            `;
        }

        container.innerHTML = `
            <header class="navbar">
                <div class="navbar-inner">
                    <a href="#/home" class="navbar-brand">
                        <div class="navbar-brand-icon">🌿</div>
                        <div>
                            <span class="navbar-brand-text">TravelWaka</span>
                            <span class="navbar-brand-sub">Jelajahi Jawa Tengah</span>
                        </div>
                    </a>

                    <nav class="navbar-links" id="navbar-links">
                        ${links}
                    </nav>

                    <div class="navbar-actions">
                        ${actions}
                        <button class="navbar-toggle" id="navbar-toggle" aria-label="Menu">
                            <span class="material-icons-round">menu</span>
                        </button>
                    </div>
                </div>
            </header>

            <div class="navbar-mobile-menu" id="navbar-mobile-menu">
                ${links}
                ${!isLoggedIn ? `
                    <div style="padding:12px 16px; display:flex; gap:8px;">
                        <a href="#/login" class="btn btn-outline w-full" style="border-color:rgba(255,255,255,0.3);color:var(--white);">Masuk</a>
                        <a href="#/register" class="btn btn-primary w-full">Daftar</a>
                    </div>
                ` : `
                    <a href="#/profile" class="navbar-link">
                        <span class="material-icons-round">person</span> Profil
                    </a>
                `}
            </div>
        `;

        // Toggle mobile menu
        const toggle = document.getElementById('navbar-toggle');
        const mobileMenu = document.getElementById('navbar-mobile-menu');
        if (toggle && mobileMenu) {
            toggle.addEventListener('click', () => {
                mobileMenu.classList.toggle('open');
            });

            // Close mobile menu on link click
            mobileMenu.querySelectorAll('a').forEach(a => {
                a.addEventListener('click', () => {
                    mobileMenu.classList.remove('open');
                });
            });
        }
    }

    // Show/hide navbar based on route (auth pages hide it)
    function setVisible(visible) {
        const container = document.getElementById('navbar-container');
        container.style.display = visible ? 'block' : 'none';
    }

    return { render, setVisible };
})();
