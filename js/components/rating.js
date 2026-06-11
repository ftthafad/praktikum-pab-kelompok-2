/* ═══════════════════════════════════════════════════════
   Rating Component — Display & Input
   ═══════════════════════════════════════════════════════ */

const Rating = (() => {
    /**
     * Render read-only stars
     * @param {number} rating — 0 to 5
     * @param {number} maxStars
     * @returns {string} HTML string
     */
    function display(rating, maxStars = 5) {
        let html = '<span class="rating-display">';
        for (let i = 1; i <= maxStars; i++) {
            if (i <= Math.floor(rating)) {
                html += '<span class="material-icons-round filled">star</span>';
            } else if (i - 0.5 <= rating) {
                html += '<span class="material-icons-round filled">star_half</span>';
            } else {
                html += '<span class="material-icons-round">star_border</span>';
            }
        }
        html += '</span>';
        return html;
    }

    /**
     * Render interactive star input
     * @param {string} containerId — id of the container element
     * @param {function} onChange — callback with (rating: number)
     * @param {number} initialValue
     */
    function input(containerId, onChange, initialValue = 0) {
        const container = document.getElementById(containerId);
        if (!container) return;

        let currentRating = initialValue;
        container.className = 'rating-input';

        function render() {
            container.innerHTML = '';
            for (let i = 1; i <= 5; i++) {
                const star = document.createElement('span');
                star.className = `material-icons-round star ${i <= currentRating ? 'filled' : ''}`;
                star.textContent = i <= currentRating ? 'star' : 'star_border';
                star.addEventListener('click', () => {
                    currentRating = i;
                    render();
                    if (onChange) onChange(currentRating);
                });
                star.addEventListener('mouseenter', () => {
                    const stars = container.querySelectorAll('.star');
                    stars.forEach((s, idx) => {
                        if (idx < i) {
                            s.classList.add('filled');
                            s.textContent = 'star';
                        } else {
                            s.classList.remove('filled');
                            s.textContent = 'star_border';
                        }
                    });
                });
                container.appendChild(star);
            }
            container.addEventListener('mouseleave', () => {
                const stars = container.querySelectorAll('.star');
                stars.forEach((s, idx) => {
                    if (idx < currentRating) {
                        s.classList.add('filled');
                        s.textContent = 'star';
                    } else {
                        s.classList.remove('filled');
                        s.textContent = 'star_border';
                    }
                });
            });
        }
        render();
    }

    return { display, input };
})();
