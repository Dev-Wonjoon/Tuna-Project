let youtubeApiPromise;

document
    .querySelectorAll('[data-youtube-playlist]')
    .forEach(initializeYoutubePlaylist);

function initializeYoutubePlaylist(root) {
    const playerElement = root.querySelector(
        '[data-youtube-player]'
    );

    const statusElement = root.querySelector('[data-youtube-status]');

    const videoIds = [
        ...new Set(
            [...root.querySelectorAll('[data-music-url]')]
                .map(element =>
                    extractYoutubeVideoId(
                        element.dataset.musicUrl
                    )
                )
                .filter(Boolean)
        )
    ];

    if(videoIds.length === 0) {
        return;
    }

    root.hidden = false;

    loadYoutubeApi().then(() => {
        new YT.Player(playerElement.id, {
            width: 640,
            height: 360,
            videoId: videoIds[0],

            playerVars: {
                playsinline: 1,
                rel: 0,
                origin: window.location.origin,
            },

            events: {
                onReady(event) {
                    event.target.cuePlaylist(
                        videoIds,
                        0,
                        0
                    );

                    const iframe = event.target.getIframe();
                    iframe.style.width = '100%';
                    iframe.style.height = '100%';

                    statusElement.textContent =
                        `${videoIds.length}곡 재생 버튼을 눌러주세요.`;
                },

                onStateChange(event) {
                    const index =
                        event.target.getPlaylistIndex();

                    if(index >= 0) {
                        statusElement.textContent =
                            `${index + 1} / ${videoIds.length}곡 재생 중`;
                    }
                },

                onError(event) {
                    statusElement.textContent =
                        `재생할 수 없는 영상입니다. 오류 코드: ${event.data}`;
                }
            }
        });
    });
}

function extractYoutubeVideoId(rawUrl) {
    if(!rawUrl) {
        return null;
    }

    try {
        const url = new URL(rawUrl.trim());

        if(
            url.protocol !== 'https:'
            && url.protocol !== 'http:'
        ) {
            return null;
        }

        const host = url.hostname
            .toLowerCase()
            .replace(/^www\./, "");

        let videoId = null;

        if(host === 'youtu.be') {
            videoId = url.pathname
                .split("/")
                .filter(Boolean)[0] ?? null;
        } else if (
            host === 'youtube.com'
            || host === 'm.youtube.com'
            || host === 'music.youtube.com'
        ) {
            if(url.pathname === '/watch') {
                videoId = url.searchParams.get('v');
            } else {
                const match = url.pathname.match(
                    /^\/(?:shorts|embed|live)\/([^/?#]+)/
                );

                videoId = match?.[1] ?? null;
            }
        } else if(host === 'youtube-nocookie.com') {
            const match = url.pathname.match(
                /^\/embed\/([^/?#]+)/
            );

            videoId = match?.[1] ?? null;
        }

        return /^[A-Za-z0-9_-]{11}$/.test(videoId ?? "")
            ? videoId
            : null;
    } catch {
        return null;
    }
}

function loadYoutubeApi() {
    if(window.YT && window.YT.Player) {
        return Promise.resolve(window.YT);
    }

    if(youtubeApiPromise) {
        return youtubeApiPromise;
    }

    youtubeApiPromise = new Promise((resolve, reject) => {
        const previousCallback =
            window.onYouTubeIframeAPIReady;

        window.onYouTubeIframeAPIReady = function () {
            if(typeof previousCallback === 'function') {
                previousCallback();
            }

            resolve(window.YT);
        };

        const script = document.createElement('script');

        script.src =
            "https://www.youtube.com/iframe_api";

        script.async = true;

        script.onerror = function () {
            youtubeApiPromise = null;

            reject(
                new Error(
                    "Youtube IFrame API를 불러오지 못했습니다."
                )
            );
        };

        document.head.appendChild(script);
    });

    return youtubeApiPromise;
}