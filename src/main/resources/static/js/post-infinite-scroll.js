document.addEventListener("DOMContentLoaded", () => {
    const root = document.querySelector(
        '[data-post-pagination]'
    );

    if(!root) {
        return;
    }

    const postList = root.querySelector(
        '[data-post-list]'
    );

    const sentinel = root.querySelector(
        '[data-post-sentinel]'
    );

    const loadingElement = root.querySelector(
        '[data-post-loading]'
    );

    const errorElement = root.querySelector(
        '[data-post-error]'
    );

    if(!postList || !sentinel) {
        return;
    }

    let nextCursor = root.dataset.nextCursor || null;

    const pageSize = root.dataset.pageSize || '10';

    let loading = false;

    if(!nextCursor) {
        sentinel.hidden = true;
        return;
    }

    const observer = new IntersectionObserver(
        entries => {
            const visible = entries.some(entry => entry.isIntersecting);

            if(visible) {
                loadNextPage();
            }
        },
        {
            root: null,
            rootMargin: '400px 0px',
        }
    );

    observer.observe(sentinel);

    errorElement?.addEventListener(
        'click',
        loadNextPage
    );

    async function loadNextPage() {
        if(loading || !nextCursor) {
            return;
        }

        loading = true;

        if(loadingElement) {
            loadingElement.hidden = false;
        }

        if(errorElement) {
            errorElement.hidden = true;
        }

        try {
            const url = new URL('/posts/page', window.location.origin);

            url.searchParams.set('cursor', nextCursor);

            url.searchParams.set('size', pageSize);

            const response = await fetch(url, {
                headers: {
                    Accept: 'text/html'
                },
            });

            if(!response.ok) {
                throw new Error(`게시글 조회 실패: ${response.status}`);
            }

            const html = await response.text();

            const parsedDocument = new DOMParser().parseFromString(
                html,
                'text/html'
            );

            const page = parsedDocument.querySelector(
                '[data-post-page]'
            );

            if(!page) {
                throw new Error('게시글 페이지 Fragment를 찾을 수 없습니다.');
            }

            const newPosts = Array.from(page.children);

            newPosts.forEach(post => {
                postList.append(post);

                post.querySelectorAll('[data-music-embed]')
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
                sentinel.hidden = true;
            }
        } catch(error) {
            console.error(error);

            if(errorElement) {
                errorElement.hidden = false;
            }
        } finally {
            loading = false;

            if(loadingElement) {
                loadingElement.hidden = true;
            }
        }
    }
});