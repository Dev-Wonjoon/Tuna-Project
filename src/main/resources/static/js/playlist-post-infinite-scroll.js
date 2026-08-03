document.addEventListener('DOMContentLoaded', () => {
    const root = document.querySelector('[data-playlist-post-pagination]');

    if(!root) {
        return;
    }

    const postList = root.querySelector('[data-playlist-post-list]');

    const sentinel = root.querySelector('[data-playlist-post-sentinel]');

    const loadingElement = root.querySelector('[data-playlist-post-loading]');

    const errorElement = root.querySelector('[data-playlist-post-error]');

    if(!postList || !sentinel) {
        return;
    }

    let nextCursor = root.dataset.nextCursor || null;

    let loading = false;

    if(!nextCursor) {
        return;
    }

    const observer = new IntersectionObserver(entries => {
        const visible = entries.some(entry => entry.isIntersecting);
            if(visible) {
                loadNextPage();
            }
        }, {
            root: null,
            rootMargin: '400px 0px',
        }
    );

    observer.observe(sentinel);

    async function loadNextPage() {
        if(loading || !nextCursor) {
            return;
        }

        loading = true;
        loadingElement.hidden = false;
        errorElement.hidden = true;

        try {
            const url = new URL(
                `/playlists/${root.dataset.playlistId}/posts/page`,
                window.location.origin
            );

            url.searchParams.set(
                'cursor',
                nextCursor
            );

            const response = await fetch(url, {
                headers: { Accept: 'text/html' },
            });

            if(!response.ok) {
                throw new Error(`게시글 조회 실패: ${response.status}`);
            }


            const html = await response.text();

            const documentFragment = new DOMParser().parseFromString(
                html, 'text/html');

            const page = documentFragment.querySelector('[data-post-page]');

            if(!page) {
                throw new Error('게시글 페이지를 찾을 수 없습니다.');
            }

            const nextElement = Array.from(page.children);

            nextElement.forEach(element => {
                postList.append(element);

                element.querySelectorAll('[data-music-embed]')
                    .forEach(musicEmbed => {
                        if(typeof initializeMusicEmbed === 'function') {
                            initializeMusicEmbed(musicEmbed);
                        }
                    });
            });

            nextCursor = page.dataset.nextCursor || null;

            root.dataset.nextCursor = nextCursor || '';

            if(!nextCursor) {
                observer.disconnect();
            }
        } catch (error) {
            console.error(error);
            errorElement.hidden = false;
        } finally {
            loading = false;
            loadingElement.hidden = true;
        }
    }
});