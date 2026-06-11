/* ═══════════════════════════════════════════════════════
   App — Router & Initialization
   ═══════════════════════════════════════════════════════ */

const App = (() => {
    let currentPage = null;
    let currentPageName = '';

    // ── Route Definitions ────────────────────────────
    const routes = {
        '#/home': { page: HomePage, showNav: true },
        '#/explore': { page: ExplorePage, showNav: true },
        '#/detail': { page: DetailPage, showNav: false, paramRoute: true },
        '#/login': { page: LoginPage, showNav: false },
        '#/register': { page: RegisterPage, showNav: false },
        '#/bookmark': { page: BookmarkPage, showNav: true },
        '#/profile': { page: ProfilePage, showNav: true },
        '#/pengajuan': { page: PengajuanPage, showNav: true },
        '#/pengelola': { page: PengelolaPage, showNav: true },
        '#/wisata/new': { page: FormWisataPage, showNav: true },
        '#/wisata/edit': { page: FormWisataPage, showNav: true, paramRoute: true },
        '#/admin': { page: AdminDashboardPage, showNav: true },
    };

    // ── Router ───────────────────────────────────────
    function resolveRoute(hash) {
        if (!hash || hash === '#/' || hash === '#') return { route: routes['#/home'], params: [] };

        // Exact match
        if (routes[hash]) return { route: routes[hash], params: [] };

        // Parameterized route matching
        // #/detail/123 → routes['#/detail'] with param '123'
        const parts = hash.split('/');

        // Check #/wisata/edit/:id
        if (parts.length === 4 && parts[1] === 'wisata' && parts[2] === 'edit') {
            return { route: routes['#/wisata/edit'], params: [parts[3]] };
        }

        // Check #/detail/:id
        if (parts.length === 3 && parts[1] === 'detail') {
            return { route: routes['#/detail'], params: [parts[2]] };
        }

        // Check #/wisata/new
        if (hash === '#/wisata/new') {
            return { route: routes['#/wisata/new'], params: [] };
        }

        // Fallback
        return { route: routes['#/home'], params: [] };
    }

    async function handleRoute() {
        const hash = location.hash || '#/home';
        const { route, params } = resolveRoute(hash);

        if (!route) {
            navigate('#/home');
            return;
        }

        // Destroy previous page
        if (currentPage && currentPage.destroy) {
            currentPage.destroy();
        }

        // Show/hide navbar
        Navbar.setVisible(route.showNav);
        if (route.showNav) {
            Navbar.render();
        }

        // Toggle page-wrapper padding
        const pageContent = document.getElementById('page-content');
        if (!route.showNav) {
            pageContent.style.paddingTop = '0';
        } else {
            pageContent.style.paddingTop = '';
        }

        // Scroll to top
        window.scrollTo(0, 0);

        // Render page
        currentPage = route.page;
        currentPageName = hash;

        if (params.length > 0) {
            await route.page.render(...params);
        } else {
            await route.page.render();
        }

        // Close mobile menu
        const mobileMenu = document.getElementById('navbar-mobile-menu');
        if (mobileMenu) mobileMenu.classList.remove('open');
    }

    // ── Navigate ─────────────────────────────────────
    function navigate(hash) {
        if (location.hash === hash) {
            // Same route — force re-render
            handleRoute();
        } else {
            location.hash = hash;
        }
    }

    // ── Init ─────────────────────────────────────────
    function init() {
        // Listen for hash changes
        window.addEventListener('hashchange', handleRoute);

        // Auth change listener
        Auth.onAuthChange(() => {
            Navbar.render();
        });

        // Default route
        if (!location.hash || location.hash === '#' || location.hash === '#/') {
            // Determine start page based on auth state
            if (Auth.isLoggedIn()) {
                const role = Auth.getUserRole();
                if (role === 'super_admin') {
                    location.hash = '#/admin';
                } else {
                    location.hash = '#/home';
                }
            } else {
                location.hash = '#/home';
            }
        }

        // Initial route handling
        handleRoute();
    }

    // ── Start ────────────────────────────────────────
    document.addEventListener('DOMContentLoaded', init);

    return { navigate, handleRoute };
})();
