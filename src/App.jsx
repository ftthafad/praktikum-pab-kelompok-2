import { useEffect } from 'react';
import { HashRouter, Routes, Route, Navigate, useLocation } from 'react-router-dom';
import { AuthProvider, useAuth } from './contexts/AuthContext';
import { ToastProvider } from './contexts/ToastContext';
import { ModalProvider } from './contexts/ModalContext';
import Navbar from './components/Navbar';

// Pages
import OnboardingPage from './pages/OnboardingPage';
import HomePage from './pages/HomePage';
import ExplorePage from './pages/ExplorePage';
import DetailPage from './pages/DetailPage';
import ReviewPage from './pages/ReviewPage';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import BookmarkPage from './pages/BookmarkPage';
import ProfilePage from './pages/ProfilePage';
import NotifikasiPage from './pages/NotifikasiPage';
import PengajuanPage from './pages/PengajuanPage';
import PengelolaPage from './pages/PengelolaPage';
import FormWisataPage from './pages/FormWisataPage';
import AdminDashboardPage from './pages/AdminDashboardPage';

// Route configuration: which routes show the navbar
const navbarRoutes = ['/home', '/explore', '/bookmark', '/profile', '/pengajuan', '/pengelola', '/wisata/new', '/wisata/edit', '/admin'];

function AppContent() {
  const location = useLocation();

  // Determine if navbar should be shown
  const showNavbar = navbarRoutes.some(route => location.pathname === route || location.pathname.startsWith(route + '/'));

  // Scroll to top on route change
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [location.pathname]);

  return (
    <>
      {showNavbar && <Navbar />}
      <main id="page-content" style={!showNavbar ? { paddingTop: 0 } : undefined}>
        <Routes>
          <Route path="/onboarding" element={<OnboardingPage />} />
          <Route path="/home" element={<HomePage />} />
          <Route path="/explore" element={<ExplorePage />} />
          <Route path="/detail/:id" element={<DetailPage />} />
          <Route path="/review/:wisataId" element={<ReviewPage />} />
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          <Route path="/bookmark" element={<BookmarkPage />} />
          <Route path="/profile" element={<ProfilePage />} />
          <Route path="/notifikasi" element={<NotifikasiPage />} />
          <Route path="/pengajuan" element={<PengajuanPage />} />
          <Route path="/pengelola" element={<PengelolaPage />} />
          <Route path="/wisata/new" element={<FormWisataPage />} />
          <Route path="/wisata/edit/:id" element={<FormWisataPage />} />
          <Route path="/admin" element={<AdminDashboardPage />} />
          <Route path="*" element={<DefaultRedirect />} />
        </Routes>
      </main>
    </>
  );
}

function DefaultRedirect() {
  const { isLoggedIn, hasSeenOnboarding, user } = useAuth();

  if (!hasSeenOnboarding()) {
    return <Navigate to="/onboarding" replace />;
  }

  if (isLoggedIn && user?.role === 'super_admin') {
    return <Navigate to="/admin" replace />;
  }

  return <Navigate to="/home" replace />;
}

export default function App() {
  return (
    <HashRouter>
      <AuthProvider>
        <ToastProvider>
          <ModalProvider>
            <AppContent />
          </ModalProvider>
        </ToastProvider>
      </AuthProvider>
    </HashRouter>
  );
}
