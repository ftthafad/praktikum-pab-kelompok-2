#!/usr/bin/env python3
"""Generate PDF User Manual for TravelWaka Website"""

from fpdf import FPDF
import os

class TravelWakaManual(FPDF):
    """Custom PDF class for TravelWaka User Manual"""
    
    def __init__(self):
        super().__init__()
        self.set_auto_page_break(auto=True, margin=25)
        # Colors
        self.primary = (53, 88, 114)       # #355872
        self.primary_light = (156, 213, 255) # #9CD5FF
        self.text_dark = (30, 30, 30)
        self.text_secondary = (100, 116, 139)
        self.white = (255, 255, 255)
        self.accent = (34, 197, 94)        # Green accent
        self.warning_color = (234, 179, 8)
        self.error_color = (220, 38, 38)
        self.bg_light = (248, 250, 252)
        
    def header(self):
        if self.page_no() == 1:
            return  # Skip header on cover page
        # Header bar
        self.set_fill_color(*self.primary)
        self.rect(0, 0, 210, 12, 'F')
        self.set_font('Helvetica', 'B', 7)
        self.set_text_color(*self.white)
        self.set_xy(10, 3)
        self.cell(0, 6, 'User Manual TravelWaka', align='L')
        self.set_font('Helvetica', '', 7)
        self.set_xy(10, 3)
        self.cell(0, 6, f'Halaman {self.page_no()}', align='R')
        self.set_text_color(*self.text_dark)
        self.set_y(18)
        
    def footer(self):
        if self.page_no() == 1:
            return
        self.set_y(-15)
        self.set_font('Helvetica', 'I', 7)
        self.set_text_color(*self.text_secondary)
        self.cell(0, 10, '(c) 2026 Kelompok 2 - TravelWaka. All Rights Reserved.', align='C')
        
    def cover_page(self):
        self.add_page()
        # Background
        self.set_fill_color(*self.primary)
        self.rect(0, 0, 210, 297, 'F')
        
        # Decorative elements
        self.set_fill_color(255, 255, 255, )
        self.set_draw_color(*self.primary_light)
        
        # Top decorative line
        self.set_fill_color(*self.primary_light)
        self.rect(0, 0, 210, 4, 'F')
        
        # Logo area
        self.set_y(70)
        self.set_font('Helvetica', '', 40)
        self.set_text_color(*self.white)
        self.cell(0, 20, '', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # App Name
        self.set_y(80)
        self.set_font('Helvetica', 'B', 42)
        self.set_text_color(*self.white)
        self.cell(0, 20, 'TravelWaka', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # Tagline
        self.set_font('Helvetica', '', 16)
        self.set_text_color(*self.primary_light)
        self.cell(0, 10, 'Jelajahi Wisata Jawa Tengah', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # Divider line
        self.set_y(self.get_y() + 15)
        x_center = 105
        self.set_draw_color(*self.primary_light)
        self.set_line_width(0.5)
        self.line(x_center - 30, self.get_y(), x_center + 30, self.get_y())
        
        # Document title
        self.set_y(self.get_y() + 20)
        self.set_font('Helvetica', 'B', 24)
        self.set_text_color(*self.white)
        self.cell(0, 12, 'USER MANUAL', align='C', new_x='LMARGIN', new_y='NEXT')
        
        self.set_font('Helvetica', '', 13)
        self.set_text_color(*self.primary_light)
        self.cell(0, 10, 'Panduan Pengguna Website', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # Version info
        self.set_y(self.get_y() + 15)
        self.set_font('Helvetica', '', 10)
        self.set_text_color(180, 210, 230)
        self.cell(0, 7, 'Versi 1.0.0 (Beta)', align='C', new_x='LMARGIN', new_y='NEXT')
        self.cell(0, 7, 'Juni 2026', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # Bottom info
        self.set_y(250)
        self.set_font('Helvetica', '', 9)
        self.set_text_color(180, 210, 230)
        self.cell(0, 6, 'Kelompok 2', align='C', new_x='LMARGIN', new_y='NEXT')
        self.cell(0, 6, 'Pemrograman Aplikasi Bergerak, Pemrograman Web, Rekayasa Perangkat Lunak', align='C', new_x='LMARGIN', new_y='NEXT')
        
        # Bottom accent
        self.set_fill_color(*self.primary_light)
        self.rect(0, 293, 210, 4, 'F')
    
    def section_title(self, number, title):
        """Section header with number"""
        self.set_y(self.get_y() + 5)
        y = self.get_y()
        
        # Number circle
        self.set_fill_color(*self.primary)
        self.set_text_color(*self.white)
        self.set_font('Helvetica', 'B', 14)
        r = 7
        self.ellipse(15, y, r*2, r*2, 'F')
        self.set_xy(15, y + 1.5)
        self.cell(r*2, r*2 - 3, str(number), align='C')
        
        # Title text
        self.set_text_color(*self.primary)
        self.set_font('Helvetica', 'B', 16)
        self.set_xy(35, y + 2)
        self.cell(0, 10, title)
        
        # Underline
        self.set_y(y + 16)
        self.set_draw_color(*self.primary_light)
        self.set_line_width(0.8)
        self.line(15, self.get_y(), 195, self.get_y())
        self.set_y(self.get_y() + 6)
        
    def sub_section(self, title):
        """Sub-section header"""
        self.set_y(self.get_y() + 3)
        self.set_font('Helvetica', 'B', 12)
        self.set_text_color(*self.primary)
        self.cell(0, 8, title, new_x='LMARGIN', new_y='NEXT')
        self.set_y(self.get_y() + 2)
        
    def sub_sub_section(self, title):
        """Sub-sub-section header"""
        self.set_y(self.get_y() + 2)
        self.set_font('Helvetica', 'B', 10)
        self.set_text_color(60, 60, 60)
        self.cell(0, 7, title, new_x='LMARGIN', new_y='NEXT')
        self.set_y(self.get_y() + 1)
        
    def body_text(self, text):
        """Normal body text"""
        self.set_font('Helvetica', '', 10)
        self.set_text_color(*self.text_dark)
        self.multi_cell(0, 6, text)
        self.set_y(self.get_y() + 2)
        
    def bullet_point(self, text, indent=20):
        """Bullet point item"""
        self.set_font('Helvetica', '', 10)
        self.set_text_color(*self.text_dark)
        x = self.get_x()
        y = self.get_y()
        
        # Bullet
        self.set_fill_color(*self.primary)
        self.ellipse(indent, y + 1.5, 2.5, 2.5, 'F')
        
        self.set_x(indent + 5)
        self.multi_cell(170 - indent, 6, text)
        self.set_y(self.get_y() + 1)
        
    def numbered_step(self, number, text):
        """Numbered step"""
        y = self.get_y()
        
        # Number box
        self.set_fill_color(*self.primary)
        self.set_text_color(*self.white)
        self.set_font('Helvetica', 'B', 9)
        self.rect(20, y, 8, 8, 'F')
        self.set_xy(20, y + 0.5)
        self.cell(8, 7, str(number), align='C')
        
        # Step text
        self.set_text_color(*self.text_dark)
        self.set_font('Helvetica', '', 10)
        self.set_xy(32, y)
        self.multi_cell(158, 6, text)
        self.set_y(self.get_y() + 2)
        
    def info_box(self, text, box_type='info'):
        """Info/warning/tip box"""
        y = self.get_y()
        
        if box_type == 'info':
            bg = (219, 234, 254)     # blue-100
            border = (59, 130, 246)  # blue-500
            icon = 'i'
        elif box_type == 'warning':
            bg = (254, 249, 195)     # yellow-100
            border = (234, 179, 8)   # yellow-500
            icon = '!'
        elif box_type == 'tip':
            bg = (220, 252, 231)     # green-100
            border = (34, 197, 94)   # green-500
            icon = '*'
        else:
            bg = (241, 245, 249)
            border = self.primary
            icon = 'i'
            
        # Calculate height
        self.set_font('Helvetica', '', 9)
        # Approximate line count
        text_width = 155
        lines = len(text) / (text_width / 2.2) + 1
        box_height = max(16, lines * 5 + 10)
        
        # Check page break
        if y + box_height > 272:
            self.add_page()
            y = self.get_y()
        
        # Background
        self.set_fill_color(*bg)
        self.rect(15, y, 180, box_height, 'F')
        
        # Left border
        self.set_fill_color(*border)
        self.rect(15, y, 3, box_height, 'F')
        
        # Text
        self.set_text_color(50, 50, 50)
        self.set_font('Helvetica', '', 9)
        self.set_xy(22, y + 4)
        self.multi_cell(168, 5, text)
        
        self.set_y(y + box_height + 4)

    def table_header(self, headers, col_widths):
        """Table header row"""
        self.set_fill_color(*self.primary)
        self.set_text_color(*self.white)
        self.set_font('Helvetica', 'B', 9)
        for i, header in enumerate(headers):
            self.cell(col_widths[i], 8, header, border=1, fill=True, align='C')
        self.ln()
        
    def table_row(self, cells, col_widths, fill=False):
        """Table data row"""
        if fill:
            self.set_fill_color(*self.bg_light)
        else:
            self.set_fill_color(*self.white)
        self.set_text_color(*self.text_dark)
        self.set_font('Helvetica', '', 9)
        for i, cell in enumerate(cells):
            align = 'L' if i == 0 else 'L'
            self.cell(col_widths[i], 7, cell, border=1, fill=fill, align=align)
        self.ln()


def generate_manual():
    pdf = TravelWakaManual()
    
    # ═══════════════════════════════════════════
    # COVER PAGE
    # ═══════════════════════════════════════════
    pdf.cover_page()
    
    # ═══════════════════════════════════════════
    # TABLE OF CONTENTS
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.set_font('Helvetica', 'B', 20)
    pdf.set_text_color(*pdf.primary)
    pdf.cell(0, 15, 'Daftar Isi', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.set_y(pdf.get_y() + 5)
    
    toc_items = [
        ('1', 'Pendahuluan', '3'),
        ('2', 'Persyaratan Sistem', '4'),
        ('3', 'Memulai Aplikasi (Onboarding)', '4'),
        ('4', 'Registrasi & Login', '5'),
        ('5', 'Beranda (Home)', '7'),
        ('6', 'Jelajahi Wisata (Explore)', '8'),
        ('7', 'Detail Wisata', '9'),
        ('8', 'Ulasan & Rating', '11'),
        ('9', 'Bookmark (Wisata Tersimpan)', '12'),
        ('10', 'Profil Pengguna', '13'),
        ('11', 'Notifikasi', '14'),
        ('12', 'Pengajuan Pengelola Wisata', '14'),
        ('13', 'Dashboard Pengelola Wisata', '15'),
        ('14', 'Dashboard Admin', '17'),
        ('15', 'Navigasi Website', '18'),
        ('16', 'FAQ (Pertanyaan Umum)', '19'),
    ]
    
    for num, title, page in toc_items:
        pdf.set_font('Helvetica', 'B' if len(num) <= 2 else '', 10)
        pdf.set_text_color(*pdf.text_dark)
        y = pdf.get_y()
        pdf.cell(10, 7, num + '.')
        pdf.cell(140, 7, title)
        pdf.set_text_color(*pdf.text_secondary)
        pdf.cell(30, 7, page, align='R')
        pdf.ln()
        # Dotted line
        pdf.set_draw_color(200, 200, 200)
        pdf.set_line_width(0.2)
        pdf.set_dash_pattern(1, 2)
        pdf.line(25, y + 7, 185, y + 7)
        pdf.set_dash_pattern()
    
    # ═══════════════════════════════════════════
    # 1. PENDAHULUAN
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(1, 'Pendahuluan')
    
    pdf.sub_section('1.1 Tentang TravelWaka')
    pdf.body_text(
        'TravelWaka adalah platform website yang dirancang untuk membantu wisatawan menjelajahi '
        'destinasi wisata terbaik di Jawa Tengah. Aplikasi ini menyediakan informasi lengkap mengenai '
        'berbagai tempat wisata, termasuk harga tiket, jam operasional, lokasi peta interaktif, '
        'foto, serta ulasan dari wisatawan lain.'
    )
    
    pdf.sub_section('1.2 Tujuan Dokumen')
    pdf.body_text(
        'Dokumen ini merupakan panduan pengguna (user manual) yang menjelaskan cara menggunakan '
        'seluruh fitur yang tersedia pada website TravelWaka. Panduan ini ditujukan untuk tiga '
        'jenis pengguna: Wisatawan (User), Pengelola Wisata, dan Admin.'
    )
    
    pdf.sub_section('1.3 Peran Pengguna')
    pdf.body_text('TravelWaka memiliki tiga peran pengguna dengan hak akses yang berbeda:')
    pdf.ln(2)
    
    col_widths = [40, 140]
    pdf.table_header(['Peran', 'Deskripsi'], col_widths)
    pdf.table_row(['Wisatawan (User)', 'Pengguna umum yang dapat menjelajahi wisata, memberi ulasan, dan menyimpan bookmark.'], col_widths)
    pdf.table_row(['Pengelola', 'Pengguna yang telah disetujui untuk mengelola (CRUD) destinasi wisata miliknya.'], col_widths, fill=True)
    pdf.table_row(['Super Admin', 'Administrator yang dapat menyetujui atau menolak pengajuan pengelola wisata.'], col_widths)
    
    # ═══════════════════════════════════════════
    # 2. PERSYARATAN SISTEM
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(2, 'Persyaratan Sistem')
    
    pdf.body_text('Untuk mengakses website TravelWaka, pastikan Anda memenuhi persyaratan berikut:')
    pdf.ln(2)
    
    pdf.sub_section('2.1 Browser yang Didukung')
    pdf.bullet_point('Google Chrome (versi 90 atau lebih baru) - Disarankan')
    pdf.bullet_point('Mozilla Firefox (versi 88 atau lebih baru)')
    pdf.bullet_point('Microsoft Edge (versi 90 atau lebih baru)')
    pdf.bullet_point('Safari (versi 14 atau lebih baru)')
    
    pdf.sub_section('2.2 Koneksi Internet')
    pdf.body_text(
        'Diperlukan koneksi internet yang stabil untuk mengakses seluruh fitur website, '
        'termasuk memuat gambar wisata, peta interaktif, dan melakukan operasi data.'
    )
    
    pdf.sub_section('2.3 Resolusi Layar')
    pdf.body_text(
        'Website TravelWaka bersifat responsif dan dapat diakses dari perangkat desktop maupun mobile. '
        'Resolusi minimum yang disarankan adalah 360px (mobile) hingga 1920px (desktop).'
    )
    
    # ═══════════════════════════════════════════
    # 3. ONBOARDING
    # ═══════════════════════════════════════════
    pdf.section_title(3, 'Memulai Aplikasi (Onboarding)')
    
    pdf.body_text(
        'Saat pertama kali mengakses website TravelWaka, Anda akan disambut oleh halaman '
        'onboarding yang memperkenalkan fitur-fitur utama aplikasi.'
    )
    
    pdf.sub_section('3.1 Langkah-langkah Onboarding')
    pdf.numbered_step(1, 'Buka website TravelWaka di browser Anda.')
    pdf.numbered_step(2, 'Halaman onboarding akan muncul dengan slide pertama: "Jelajahi Wisata Jawa Tengah".')
    pdf.numbered_step(3, 'Tekan tombol "Selanjutnya" atau geser (swipe) untuk melihat slide berikutnya.')
    pdf.numbered_step(4, 'Terdapat 3 slide yang menjelaskan fitur utama:')
    pdf.bullet_point('Slide 1: Jelajahi Wisata Jawa Tengah - Temukan ratusan destinasi wisata.', 35)
    pdf.bullet_point('Slide 2: Informasi Lengkap & Terpercaya - Info harga, jam operasional, dan fasilitas.', 35)
    pdf.bullet_point('Slide 3: Rencanakan Perjalananmu - Simpan favorit dan baca ulasan.', 35)
    pdf.numbered_step(5, 'Pada slide terakhir, tekan "Mulai Jelajahi" untuk melanjutkan ke halaman login.')
    
    pdf.info_box('Tips: Anda dapat menekan tombol "Skip" di pojok kanan atas untuk melewati onboarding dan langsung menuju halaman login.', 'tip')
    
    # ═══════════════════════════════════════════
    # 4. REGISTRASI & LOGIN
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(4, 'Registrasi & Login')
    
    pdf.sub_section('4.1 Registrasi (Daftar Akun Baru)')
    pdf.body_text('Untuk membuat akun baru di TravelWaka, ikuti langkah berikut:')
    pdf.numbered_step(1, 'Pada halaman login, klik tautan "Daftar" di bagian bawah halaman.')
    pdf.numbered_step(2, 'Isi formulir registrasi dengan data berikut:')
    pdf.bullet_point('Nama Lengkap: Masukkan nama lengkap Anda.', 35)
    pdf.bullet_point('Email: Masukkan alamat email yang valid.', 35)
    pdf.bullet_point('Password: Buat password minimal 8 karakter.', 35)
    pdf.bullet_point('Konfirmasi Password: Ulangi password yang sama.', 35)
    pdf.numbered_step(3, 'Klik tombol "Daftar" untuk mengirim formulir.')
    pdf.numbered_step(4, 'Jika berhasil, Anda akan otomatis masuk dan diarahkan ke halaman Beranda.')
    
    pdf.info_box('Catatan: Password harus minimal 8 karakter. Pastikan password dan konfirmasi password sama.', 'warning')
    
    pdf.sub_section('4.2 Login (Masuk)')
    pdf.body_text('Untuk masuk ke akun yang sudah terdaftar:')
    pdf.numbered_step(1, 'Buka halaman login TravelWaka.')
    pdf.numbered_step(2, 'Masukkan email dan password Anda.')
    pdf.numbered_step(3, 'Klik tombol "Masuk".')
    pdf.numbered_step(4, 'Jika berhasil, Anda akan diarahkan ke halaman Beranda (untuk user biasa) atau Dashboard Admin (untuk super admin).')
    
    pdf.sub_section('4.3 Login dengan Google')
    pdf.body_text(
        'TravelWaka mendukung login menggunakan akun Google untuk kemudahan akses:'
    )
    pdf.numbered_step(1, 'Pada halaman login, klik tombol "Masuk dengan Google".')
    pdf.numbered_step(2, 'Pilih akun Google yang ingin digunakan dari popup Google Sign-In.')
    pdf.numbered_step(3, 'Setelah berhasil, Anda akan otomatis masuk ke TravelWaka.')
    
    pdf.sub_section('4.4 Akses Tanpa Login')
    pdf.body_text(
        'Anda juga dapat menjelajahi wisata tanpa login dengan menekan tombol "Lewati, Jelajahi Dulu" '
        'pada halaman login. Namun, beberapa fitur seperti bookmark, ulasan, dan profil memerlukan login.'
    )
    
    # ═══════════════════════════════════════════
    # 5. BERANDA (HOME)
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(5, 'Beranda (Home)')
    
    pdf.body_text(
        'Halaman Beranda adalah tampilan utama setelah login. Halaman ini menampilkan rekomendasi '
        'wisata dan berbagai kategori yang tersedia.'
    )
    
    pdf.sub_section('5.1 Banner Carousel')
    pdf.body_text(
        'Di bagian atas halaman Beranda terdapat banner carousel yang menampilkan gambar-gambar '
        'wisata secara bergantian setiap 5 detik. Anda dapat mengklik indikator titik (dots) '
        'di bagian bawah banner untuk berpindah slide secara manual.'
    )
    
    pdf.sub_section('5.2 Kategori Wisata')
    pdf.body_text(
        'Di bawah banner, terdapat filter kategori berupa chip/tag yang dapat diklik. '
        'Pilih kategori tertentu untuk memfilter wisata yang ditampilkan, atau pilih "Semua" '
        'untuk menampilkan seluruh wisata.'
    )
    
    pdf.sub_section('5.3 Wisata Populer')
    pdf.body_text(
        'Bagian ini menampilkan hingga 6 wisata dengan rating tertinggi dalam format kartu horizontal '
        'yang dapat di-scroll ke kanan. Setiap kartu menampilkan:'
    )
    pdf.bullet_point('Foto wisata')
    pdf.bullet_point('Nama wisata')
    pdf.bullet_point('Lokasi')
    pdf.bullet_point('Rating bintang')
    pdf.bullet_point('Harga tiket')
    pdf.body_text('Klik "Lihat Semua" untuk membuka halaman Jelajahi (Explore) yang lebih lengkap.')
    
    pdf.sub_section('5.4 Semua Wisata')
    pdf.body_text(
        'Di bagian bawah halaman, ditampilkan daftar seluruh wisata dalam format list card. '
        'Klik salah satu kartu untuk melihat detail wisata tersebut.'
    )
    
    # ═══════════════════════════════════════════
    # 6. JELAJAHI WISATA (EXPLORE)
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(6, 'Jelajahi Wisata (Explore)')
    
    pdf.body_text(
        'Halaman Jelajahi menyediakan fitur pencarian dan filter yang lebih lengkap untuk '
        'menemukan destinasi wisata yang Anda inginkan.'
    )
    
    pdf.sub_section('6.1 Pencarian Wisata')
    pdf.body_text('Gunakan kolom pencarian di bagian atas halaman untuk mencari wisata:')
    pdf.numbered_step(1, 'Ketik kata kunci pada kolom "Cari destinasi wisata...".')
    pdf.numbered_step(2, 'Hasil pencarian akan otomatis ditampilkan saat Anda mengetik.')
    pdf.numbered_step(3, 'Pencarian mencakup nama wisata, lokasi, dan nama kategori.')
    
    pdf.sub_section('6.2 Filter Kategori')
    pdf.body_text(
        'Gunakan chip kategori untuk memfilter wisata berdasarkan kategori tertentu. '
        'Pilih "Semua" untuk menampilkan seluruh wisata tanpa filter.'
    )
    
    pdf.sub_section('6.3 Hasil Pencarian')
    pdf.body_text(
        'Jumlah destinasi yang ditemukan ditampilkan di bawah filter kategori. '
        'Wisata ditampilkan dalam format grid kartu. Klik kartu untuk melihat detail wisata.'
    )
    
    pdf.info_box('Tips: Anda dapat mengombinasikan pencarian teks dengan filter kategori untuk hasil yang lebih spesifik.', 'tip')
    
    # ═══════════════════════════════════════════
    # 7. DETAIL WISATA
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(7, 'Detail Wisata')
    
    pdf.body_text(
        'Halaman Detail Wisata menampilkan informasi lengkap tentang satu destinasi wisata, '
        'termasuk foto, deskripsi, lokasi di peta, dan ulasan pengunjung.'
    )
    
    pdf.sub_section('7.1 Galeri Foto')
    pdf.body_text(
        'Di bagian atas terdapat galeri foto wisata yang dapat dinavigasi menggunakan indikator '
        'titik. Jika wisata belum memiliki foto, akan ditampilkan placeholder ikon.'
    )
    
    pdf.sub_section('7.2 Tombol Aksi (Header)')
    pdf.body_text('Di atas galeri foto terdapat beberapa tombol aksi:')
    pdf.bullet_point('Tombol Kembali (panah kiri): Kembali ke halaman sebelumnya.')
    pdf.bullet_point('Tombol Bookmark: Simpan/hapus wisata dari daftar bookmark Anda. Ikon berubah menjadi solid jika sudah disimpan.')
    pdf.bullet_point('Tombol Share: Bagikan informasi wisata melalui fitur share bawaan browser atau salin ke clipboard.')
    
    pdf.sub_section('7.3 Informasi Utama')
    pdf.body_text('Kartu informasi utama menampilkan:')
    pdf.bullet_point('Nama wisata')
    pdf.bullet_point('Lokasi (alamat)')
    pdf.bullet_point('Rating bintang (skala 1-5)')
    pdf.bullet_point('Harga tiket')
    pdf.bullet_point('Kategori wisata')
    pdf.bullet_point('Jam operasional')
    pdf.bullet_point('Deskripsi lengkap wisata')
    
    pdf.sub_section('7.4 Peta Interaktif (Leaflet)')
    pdf.body_text(
        'Jika data koordinat tersedia, halaman detail menampilkan peta interaktif menggunakan '
        'Leaflet dan OpenStreetMap. Peta menunjukkan lokasi wisata dengan marker. '
        'Klik tombol "Get Direction" untuk membuka Google Maps dengan navigasi ke lokasi wisata.'
    )
    
    pdf.sub_section('7.5 Ulasan Pengunjung')
    pdf.body_text(
        'Di bagian bawah halaman detail, ditampilkan daftar ulasan dari pengunjung lain. '
        'Setiap ulasan menampilkan nama pengguna, rating, tanggal, dan komentar. '
        'Klik tombol "Tulis Ulasan" untuk menulis ulasan Anda sendiri.'
    )
    
    # ═══════════════════════════════════════════
    # 8. ULASAN & RATING
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(8, 'Ulasan & Rating')
    
    pdf.body_text(
        'Fitur ulasan memungkinkan pengguna yang sudah login untuk memberikan penilaian dan '
        'komentar terhadap wisata yang pernah dikunjungi.'
    )
    
    pdf.sub_section('8.1 Menulis Ulasan Baru')
    pdf.numbered_step(1, 'Buka halaman Detail Wisata yang ingin diulas.')
    pdf.numbered_step(2, 'Klik tombol "Tulis Ulasan" di bagian ulasan.')
    pdf.numbered_step(3, 'Pilih rating bintang (1-5) dengan mengklik bintang yang sesuai:')
    
    pdf.ln(2)
    col_widths = [30, 150]
    pdf.table_header(['Bintang', 'Keterangan'], col_widths)
    pdf.table_row(['1', 'Sangat Buruk'], col_widths)
    pdf.table_row(['2', 'Buruk'], col_widths, fill=True)
    pdf.table_row(['3', 'Cukup'], col_widths)
    pdf.table_row(['4', 'Bagus'], col_widths, fill=True)
    pdf.table_row(['5', 'Luar Biasa!'], col_widths)
    pdf.ln(2)
    
    pdf.numbered_step(4, 'Tulis komentar ulasan Anda (opsional, maksimal 500 karakter).')
    pdf.numbered_step(5, 'Klik tombol "Kirim Ulasan" untuk mengirimkan ulasan.')
    
    pdf.sub_section('8.2 Mengedit Ulasan')
    pdf.body_text(
        'Jika Anda sudah pernah memberikan ulasan pada wisata tertentu, halaman ulasan akan '
        'otomatis memuat ulasan sebelumnya. Anda dapat mengubah rating dan komentar, lalu klik '
        '"Simpan Perubahan" untuk memperbarui ulasan.'
    )
    
    pdf.info_box('Catatan: Anda harus login terlebih dahulu untuk menulis ulasan. Setiap pengguna hanya dapat memberikan satu ulasan per wisata.', 'info')
    
    # ═══════════════════════════════════════════
    # 9. BOOKMARK
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(9, 'Bookmark (Wisata Tersimpan)')
    
    pdf.body_text(
        'Fitur bookmark memungkinkan Anda menyimpan wisata favorit untuk diakses kembali dengan mudah.'
    )
    
    pdf.sub_section('9.1 Menyimpan Wisata ke Bookmark')
    pdf.numbered_step(1, 'Buka halaman Detail Wisata yang ingin disimpan.')
    pdf.numbered_step(2, 'Klik ikon bookmark (ikon penanda buku) di bagian atas galeri foto.')
    pdf.numbered_step(3, 'Notifikasi "Disimpan ke bookmark!" akan muncul jika berhasil.')
    
    pdf.sub_section('9.2 Menghapus Bookmark')
    pdf.numbered_step(1, 'Buka halaman Detail Wisata yang sudah di-bookmark.')
    pdf.numbered_step(2, 'Klik kembali ikon bookmark (yang sudah berubah solid).')
    pdf.numbered_step(3, 'Notifikasi "Dihapus dari bookmark" akan muncul.')
    
    pdf.sub_section('9.3 Melihat Daftar Bookmark')
    pdf.body_text(
        'Klik menu "Tersimpan" pada navbar untuk membuka halaman Bookmark. '
        'Halaman ini menampilkan semua wisata yang telah Anda simpan dalam format grid kartu. '
        'Klik kartu untuk melihat detail wisata.'
    )
    
    pdf.info_box('Catatan: Fitur bookmark hanya tersedia untuk pengguna yang sudah login. Menu "Tersimpan" hanya muncul di navbar jika Anda sudah login.', 'info')
    
    # ═══════════════════════════════════════════
    # 10. PROFIL
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(10, 'Profil Pengguna')
    
    pdf.body_text(
        'Halaman Profil menampilkan informasi akun Anda beserta menu navigasi ke berbagai fitur.'
    )
    
    pdf.sub_section('10.1 Informasi Profil')
    pdf.body_text('Di bagian atas halaman profil ditampilkan:')
    pdf.bullet_point('Avatar pengguna (inisial nama jika tidak ada foto)')
    pdf.bullet_point('Nama lengkap')
    pdf.bullet_point('Alamat email')
    pdf.bullet_point('Label peran (Wisatawan / Pengelola Wisata / Super Admin)')
    
    pdf.sub_section('10.2 Statistik Pengguna')
    pdf.body_text('Kartu statistik menampilkan:')
    pdf.bullet_point('Jumlah Bookmark: Total wisata yang disimpan.')
    pdf.bullet_point('Jumlah Ulasan: Total ulasan yang pernah ditulis.')
    pdf.bullet_point('Wisata Saya: Total wisata yang dikelola (hanya untuk peran Pengelola).')
    
    pdf.sub_section('10.3 Menu Profil')
    pdf.body_text('Menu yang tersedia di halaman profil:')
    
    col_widths = [50, 130]
    pdf.table_header(['Menu', 'Keterangan'], col_widths)
    pdf.table_row(['Notifikasi', 'Melihat status pengajuan pengelola'], col_widths)
    pdf.table_row(['Hubungi Bantuan', 'Chat admin via WhatsApp'], col_widths, fill=True)
    pdf.table_row(['Tentang Aplikasi', 'Informasi versi dan pengembang'], col_widths)
    pdf.table_row(['Ajukan Jadi Pengelola', 'Formulir pengajuan (hanya untuk Wisatawan)'], col_widths, fill=True)
    pdf.table_row(['Kelola Wisata', 'Dashboard pengelola (hanya untuk Pengelola)'], col_widths)
    pdf.table_row(['Keluar', 'Logout dari akun'], col_widths, fill=True)
    
    pdf.sub_section('10.4 Logout')
    pdf.body_text(
        'Untuk keluar dari akun, klik tombol "Keluar" di bagian bawah halaman profil. '
        'Sebuah dialog konfirmasi akan muncul. Klik "Keluar" untuk mengonfirmasi.'
    )
    
    # ═══════════════════════════════════════════
    # 11. NOTIFIKASI
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(11, 'Notifikasi')
    
    pdf.body_text(
        'Halaman Notifikasi menampilkan status terkini dari pengajuan pengelola wisata yang '
        'pernah Anda kirimkan.'
    )
    
    pdf.sub_section('11.1 Status Pengajuan')
    pdf.body_text('Status pengajuan yang dapat ditampilkan:')
    
    col_widths = [35, 40, 105]
    pdf.table_header(['Status', 'Ikon', 'Keterangan'], col_widths)
    pdf.table_row(['Menunggu', 'Hourglass', 'Pengajuan sedang dalam proses review oleh admin.'], col_widths)
    pdf.table_row(['Disetujui', 'Check Circle', 'Pengajuan disetujui, Anda bisa mengelola wisata.'], col_widths, fill=True)
    pdf.table_row(['Ditolak', 'Cancel', 'Pengajuan ditolak, disertai catatan admin.'], col_widths)
    
    pdf.sub_section('11.2 Detail Notifikasi')
    pdf.body_text(
        'Setiap notifikasi menampilkan detail pengajuan seperti nama usaha, deskripsi, alasan, '
        'dan catatan admin (jika ditolak). Pesan status juga ditampilkan untuk memberitahu '
        'langkah selanjutnya.'
    )
    
    # ═══════════════════════════════════════════
    # 12. PENGAJUAN PENGELOLA
    # ═══════════════════════════════════════════
    pdf.section_title(12, 'Pengajuan Pengelola Wisata')
    
    pdf.body_text(
        'Pengguna dengan peran Wisatawan dapat mengajukan diri untuk menjadi Pengelola Wisata '
        'melalui halaman ini.'
    )
    
    pdf.sub_section('12.1 Mengajukan Diri sebagai Pengelola')
    pdf.numbered_step(1, 'Buka halaman Profil, lalu klik "Ajukan Jadi Pengelola".')
    pdf.numbered_step(2, 'Isi formulir pengajuan:')
    pdf.bullet_point('Nama Usaha: Nama usaha wisata yang Anda kelola.', 35)
    pdf.bullet_point('Deskripsi Usaha: Penjelasan singkat tentang usaha wisata Anda.', 35)
    pdf.bullet_point('Alasan Pengajuan: Alasan mengapa Anda ingin menjadi pengelola.', 35)
    pdf.numbered_step(3, 'Klik "Kirim Pengajuan" untuk mengirim formulir.')
    pdf.numbered_step(4, 'Status pengajuan akan ditampilkan di halaman ini dan di halaman Notifikasi.')
    
    pdf.sub_section('12.2 Status Pengajuan')
    pdf.body_text(
        'Setelah mengirim pengajuan, halaman akan menampilkan status pengajuan Anda beserta '
        'detail yang telah dikirimkan. Jika ditolak, catatan admin juga akan ditampilkan.'
    )
    
    # ═══════════════════════════════════════════
    # 13. DASHBOARD PENGELOLA
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(13, 'Dashboard Pengelola Wisata')
    
    pdf.body_text(
        'Dashboard Pengelola hanya dapat diakses oleh pengguna dengan peran Pengelola Wisata. '
        'Dari dashboard ini, Anda dapat mengelola destinasi wisata milik Anda.'
    )
    
    pdf.sub_section('13.1 Melihat Daftar Wisata')
    pdf.body_text(
        'Halaman "Wisata Saya" menampilkan daftar seluruh wisata yang Anda kelola. '
        'Setiap kartu wisata menampilkan foto, nama, dan lokasi, beserta tombol Edit dan Hapus.'
    )
    
    pdf.sub_section('13.2 Menambah Wisata Baru')
    pdf.numbered_step(1, 'Klik tombol "Tambah" di pojok kanan atas halaman Wisata Saya.')
    pdf.numbered_step(2, 'Isi formulir "Tambah Wisata Baru" dengan data berikut:')
    
    col_widths = [45, 80, 55]
    pdf.table_header(['Field', 'Keterangan', 'Wajib'], col_widths)
    pdf.table_row(['Nama Wisata', 'Nama destinasi wisata', 'Ya'], col_widths)
    pdf.table_row(['Kategori', 'Pilih kategori dari dropdown', 'Ya'], col_widths, fill=True)
    pdf.table_row(['Deskripsi', 'Deskripsi lengkap wisata', 'Ya'], col_widths)
    pdf.table_row(['Lokasi', 'Alamat atau lokasi wisata', 'Ya'], col_widths, fill=True)
    pdf.table_row(['Latitude', 'Koordinat lintang (contoh: -7.12)', 'Tidak'], col_widths)
    pdf.table_row(['Longitude', 'Koordinat bujur (contoh: 110.42)', 'Tidak'], col_widths, fill=True)
    pdf.table_row(['Harga Tiket', 'Harga tiket masuk (contoh: Rp 25.000)', 'Ya'], col_widths)
    pdf.table_row(['Jam Operasional', 'Jam buka-tutup (contoh: 08:00-17:00)', 'Tidak'], col_widths, fill=True)
    
    pdf.ln(2)
    pdf.numbered_step(3, 'Klik "Tambah Wisata" untuk menyimpan.')
    pdf.numbered_step(4, 'Anda akan diarahkan kembali ke halaman Wisata Saya.')
    
    pdf.sub_section('13.3 Mengedit Wisata')
    pdf.numbered_step(1, 'Pada kartu wisata, klik tombol "Edit".')
    pdf.numbered_step(2, 'Formulir edit akan muncul dengan data yang sudah terisi.')
    pdf.numbered_step(3, 'Ubah data yang diperlukan.')
    pdf.numbered_step(4, 'Klik "Simpan Perubahan" untuk menyimpan.')
    
    pdf.sub_section('13.4 Mengelola Foto Wisata')
    pdf.body_text('Pada mode edit wisata, Anda dapat mengelola foto:')
    pdf.bullet_point('Upload Foto: Klik area upload, pilih file gambar dari perangkat Anda.')
    pdf.bullet_point('Hapus Foto: Klik tombol X pada foto yang ingin dihapus.')
    
    pdf.info_box('Catatan: Upload foto hanya tersedia pada mode edit (wisata yang sudah tersimpan). Tambahkan wisata terlebih dahulu, kemudian edit untuk menambahkan foto.', 'warning')
    
    pdf.sub_section('13.5 Menghapus Wisata')
    pdf.numbered_step(1, 'Pada kartu wisata, klik tombol "Hapus".')
    pdf.numbered_step(2, 'Dialog konfirmasi akan muncul.')
    pdf.numbered_step(3, 'Klik "Hapus" untuk mengonfirmasi penghapusan permanen.')
    
    pdf.info_box('Peringatan: Penghapusan wisata bersifat permanen dan tidak dapat dibatalkan. Semua data termasuk foto dan ulasan terkait akan ikut terhapus.', 'warning')
    
    # ═══════════════════════════════════════════
    # 14. DASHBOARD ADMIN
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(14, 'Dashboard Admin')
    
    pdf.body_text(
        'Dashboard Admin hanya dapat diakses oleh pengguna dengan peran Super Admin. '
        'Dashboard ini digunakan untuk mengelola pengajuan pengelola wisata.'
    )
    
    pdf.sub_section('14.1 Statistik Pengajuan')
    pdf.body_text('Di bagian atas dashboard, terdapat kartu statistik yang menampilkan:')
    pdf.bullet_point('Total Pengajuan: Jumlah seluruh pengajuan yang masuk.')
    pdf.bullet_point('Menunggu: Jumlah pengajuan yang belum diproses.')
    pdf.bullet_point('Disetujui: Jumlah pengajuan yang telah disetujui.')
    pdf.bullet_point('Ditolak: Jumlah pengajuan yang ditolak.')
    
    pdf.sub_section('14.2 Tab Filter')
    pdf.body_text(
        'Gunakan tab "Menunggu", "Disetujui", dan "Ditolak" untuk memfilter daftar pengajuan '
        'berdasarkan status.'
    )
    
    pdf.sub_section('14.3 Menyetujui Pengajuan')
    pdf.numbered_step(1, 'Buka tab "Menunggu" untuk melihat pengajuan yang belum diproses.')
    pdf.numbered_step(2, 'Review detail pengajuan (nama usaha, deskripsi, alasan, dan info pemohon).')
    pdf.numbered_step(3, 'Klik tombol "Setujui" pada pengajuan yang ingin disetujui.')
    pdf.numbered_step(4, 'Konfirmasi pada dialog yang muncul.')
    pdf.numbered_step(5, 'Pengguna pemohon akan otomatis mendapat peran Pengelola Wisata.')
    
    pdf.sub_section('14.4 Menolak Pengajuan')
    pdf.numbered_step(1, 'Klik tombol "Tolak" pada pengajuan yang ingin ditolak.')
    pdf.numbered_step(2, 'Tulis catatan/alasan penolakan pada dialog prompt yang muncul.')
    pdf.numbered_step(3, 'Klik "Tolak" untuk mengonfirmasi penolakan.')
    
    pdf.info_box('Catatan: Catatan penolakan wajib diisi dan akan ditampilkan kepada pemohon melalui halaman Notifikasi.', 'info')
    
    pdf.sub_section('14.5 Logout Admin')
    pdf.body_text(
        'Klik tombol "Keluar" di pojok kanan atas dashboard. Konfirmasi pada dialog yang muncul '
        'untuk keluar dari akun admin.'
    )
    
    # ═══════════════════════════════════════════
    # 15. NAVIGASI WEBSITE
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(15, 'Navigasi Website')
    
    pdf.sub_section('15.1 Navbar (Bilah Navigasi)')
    pdf.body_text(
        'Navbar terletak di bagian atas website dan menampilkan logo TravelWaka beserta menu navigasi. '
        'Tampilan navbar berbeda berdasarkan status login dan peran pengguna.'
    )
    
    pdf.sub_sub_section('Pengguna Belum Login:')
    pdf.bullet_point('Menu: Beranda, Jelajahi')
    pdf.bullet_point('Tombol: Masuk, Daftar')
    
    pdf.sub_sub_section('Wisatawan (Sudah Login):')
    pdf.bullet_point('Menu: Beranda, Jelajahi, Tersimpan')
    pdf.bullet_point('Avatar profil di pojok kanan (klik untuk ke halaman Profil)')
    
    pdf.sub_sub_section('Pengelola Wisata (Sudah Login):')
    pdf.bullet_point('Sama seperti Wisatawan, dengan tambahan akses ke menu Kelola Wisata di Profil')
    
    pdf.sub_sub_section('Super Admin:')
    pdf.bullet_point('Menu: Dashboard (langsung ke Dashboard Admin)')
    pdf.bullet_point('Tombol Keluar di pojok kanan atas')
    
    pdf.sub_section('15.2 Menu Mobile')
    pdf.body_text(
        'Pada layar kecil (mobile), menu navigasi digantikan oleh tombol hamburger (ikon tiga garis). '
        'Klik tombol ini untuk membuka/menutup menu mobile yang menampilkan seluruh link navigasi.'
    )
    
    pdf.sub_section('15.3 Peta Navigasi Halaman')
    pdf.body_text('Berikut adalah peta navigasi lengkap website TravelWaka:')
    pdf.ln(2)
    
    col_widths = [45, 50, 85]
    pdf.table_header(['Halaman', 'URL', 'Akses'], col_widths)
    pdf.table_row(['Onboarding', '/#/onboarding', 'Semua (pertama kali)'], col_widths)
    pdf.table_row(['Login', '/#/login', 'Semua'], col_widths, fill=True)
    pdf.table_row(['Register', '/#/register', 'Semua'], col_widths)
    pdf.table_row(['Beranda', '/#/home', 'Semua'], col_widths, fill=True)
    pdf.table_row(['Jelajahi', '/#/explore', 'Semua'], col_widths)
    pdf.table_row(['Detail Wisata', '/#/detail/:id', 'Semua'], col_widths, fill=True)
    pdf.table_row(['Tulis Ulasan', '/#/review/:id', 'Login required'], col_widths)
    pdf.table_row(['Bookmark', '/#/bookmark', 'Login required'], col_widths, fill=True)
    pdf.table_row(['Profil', '/#/profile', 'Login required'], col_widths)
    pdf.table_row(['Notifikasi', '/#/notifikasi', 'Login required'], col_widths, fill=True)
    pdf.table_row(['Pengajuan', '/#/pengajuan', 'Wisatawan'], col_widths)
    pdf.table_row(['Kelola Wisata', '/#/pengelola', 'Pengelola'], col_widths, fill=True)
    pdf.table_row(['Form Wisata', '/#/wisata/new', 'Pengelola'], col_widths)
    pdf.table_row(['Edit Wisata', '/#/wisata/edit/:id', 'Pengelola'], col_widths, fill=True)
    pdf.table_row(['Dashboard Admin', '/#/admin', 'Super Admin'], col_widths)
    
    # ═══════════════════════════════════════════
    # 16. FAQ
    # ═══════════════════════════════════════════
    pdf.add_page()
    pdf.section_title(16, 'FAQ (Pertanyaan Umum)')
    
    faq_items = [
        (
            'Apakah saya bisa menjelajahi wisata tanpa membuat akun?',
            'Ya, Anda dapat menekan tombol "Lewati, Jelajahi Dulu" pada halaman login untuk mengakses halaman Beranda dan Jelajahi tanpa login. Namun, fitur bookmark, ulasan, dan profil memerlukan akun.'
        ),
        (
            'Bagaimana cara menjadi Pengelola Wisata?',
            'Buat akun terlebih dahulu, lalu buka halaman Profil dan pilih "Ajukan Jadi Pengelola". Isi formulir pengajuan dan tunggu persetujuan dari admin.'
        ),
        (
            'Saya sudah mengajukan menjadi pengelola, berapa lama prosesnya?',
            'Proses review dilakukan oleh admin. Anda dapat memantau status pengajuan melalui halaman Notifikasi di menu Profil.'
        ),
        (
            'Bagaimana cara menambah foto pada wisata yang saya kelola?',
            'Buat wisata terlebih dahulu, lalu edit wisata tersebut. Pada halaman edit, tersedia area upload foto di bagian bawah formulir.'
        ),
        (
            'Bisakah saya mengedit ulasan yang sudah saya kirim?',
            'Ya, jika Anda membuka halaman tulis ulasan untuk wisata yang sudah pernah diulas, data ulasan sebelumnya akan otomatis dimuat. Anda dapat mengubah rating dan komentar, lalu klik "Simpan Perubahan".'
        ),
        (
            'Bagaimana cara menghubungi admin jika ada masalah?',
            'Buka halaman Profil, lalu klik "Hubungi Bantuan". Anda akan diarahkan ke WhatsApp untuk menghubungi admin TravelWaka.'
        ),
        (
            'Apakah data saya aman?',
            'TravelWaka menggunakan autentikasi berbasis token (Bearer Token) untuk melindungi data pengguna. Password disimpan dalam bentuk terenkripsi.'
        ),
        (
            'Website tidak bisa dimuat, apa yang harus saya lakukan?',
            'Periksa koneksi internet Anda. Pastikan browser Anda mendukung JavaScript dan cookies. Coba muat ulang halaman (refresh). Jika masalah berlanjut, hubungi admin.'
        ),
    ]
    
    for i, (q, a) in enumerate(faq_items):
        if pdf.get_y() > 250:
            pdf.add_page()
        
        # Question
        pdf.set_font('Helvetica', 'B', 10)
        pdf.set_text_color(*pdf.primary)
        pdf.set_x(15)
        pdf.multi_cell(180, 6, f'Q{i+1}: {q}')
        pdf.set_y(pdf.get_y() + 1)
        
        # Answer
        pdf.set_font('Helvetica', '', 10)
        pdf.set_text_color(*pdf.text_dark)
        pdf.set_x(15)
        pdf.multi_cell(180, 6, f'A: {a}')
        pdf.set_y(pdf.get_y() + 5)
    
    # ═══════════════════════════════════════════
    # CLOSING PAGE
    # ═══════════════════════════════════════════
    pdf.add_page()
    
    # Centered closing
    pdf.set_y(80)
    pdf.set_font('Helvetica', 'B', 24)
    pdf.set_text_color(*pdf.primary)
    pdf.cell(0, 15, 'Terima Kasih', align='C', new_x='LMARGIN', new_y='NEXT')
    
    pdf.set_font('Helvetica', '', 12)
    pdf.set_text_color(*pdf.text_secondary)
    pdf.cell(0, 8, 'Terima kasih telah menggunakan TravelWaka!', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.cell(0, 8, 'Semoga panduan ini membantu Anda dalam menggunakan website.', align='C', new_x='LMARGIN', new_y='NEXT')
    
    pdf.set_y(pdf.get_y() + 20)
    
    # Divider
    pdf.set_draw_color(*pdf.primary_light)
    pdf.set_line_width(0.5)
    pdf.line(70, pdf.get_y(), 140, pdf.get_y())
    
    pdf.set_y(pdf.get_y() + 15)
    pdf.set_font('Helvetica', 'B', 11)
    pdf.set_text_color(*pdf.primary)
    pdf.cell(0, 8, 'Kontak & Dukungan', align='C', new_x='LMARGIN', new_y='NEXT')
    
    pdf.set_font('Helvetica', '', 10)
    pdf.set_text_color(*pdf.text_secondary)
    pdf.cell(0, 7, 'Jika Anda membutuhkan bantuan lebih lanjut,', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.cell(0, 7, 'hubungi admin melalui fitur "Hubungi Bantuan" di halaman Profil.', align='C', new_x='LMARGIN', new_y='NEXT')
    
    pdf.set_y(pdf.get_y() + 30)
    pdf.set_font('Helvetica', 'I', 9)
    pdf.set_text_color(180, 180, 180)
    pdf.cell(0, 7, 'TravelWaka v1.0.0 (Beta)', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.cell(0, 7, '(c) 2026 Kelompok 2. All Rights Reserved.', align='C', new_x='LMARGIN', new_y='NEXT')
    pdf.cell(0, 7, 'Pemrograman Aplikasi Bergerak, Pemrograman Web, Rekayasa Perangkat Lunak', align='C', new_x='LMARGIN', new_y='NEXT')
    
    # ═══════════════════════════════════════════
    # SAVE
    # ═══════════════════════════════════════════
    output_path = os.path.join(os.path.dirname(os.path.abspath(__file__)), 'User_Manual_TravelWaka.pdf')
    pdf.output(output_path)
    print(f'User manual generated successfully: {output_path}')
    return output_path


if __name__ == '__main__':
    generate_manual()
