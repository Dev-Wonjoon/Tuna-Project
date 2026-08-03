let youtubeApiPromise;

document
    .querySelectorAll('[data-youtube-playlist]')
    .forEach(initializeYoutubePlaylist);

function initializeYoutubePlaylist(root) {
    const playerElement = root.querySelector(
        '[data-youtube-player]'
    );

    const statusElement = root.querySelector('[data-youtube-status]');

    let videoIds = [
        ...new Set(
            [...root.querySelectorAll('[data-youtube-video-id]')]
                .map(element => element.dataset.youtubeVideoId)
                .filter(Boolean)
        )
    ];

    let nextCursor = root.dataset.nextCursor || null;

    let nextBatchPromise = null;

    let lastKnownIndex = -1;

    function requestNextBatch() {
        if(nextBatchPromise) {
            return nextBatchPromise;
        }

        if(!nextCursor) {
            return Promise.resolve([]);
        }

        const url = new URL(`/playlists/${root.dataset.playlistId}/youtube-tracks`, window.location.origin);

        url.searchParams.set('cursor', nextCursor);

        nextBatchPromise = fetch(url, {
            headers: {
                Accept: 'application/json',
            }
        })
            .then(response => {
                if(!response.ok) {
                    throw new Error(
                        '다음 YouTube 목록을 불러오지 못했습니다.'
                    );
                }

                return response.json();
            })
            .then(slice => {
                nextCursor = slice.nextCursor || null;

                root.dataset.nextCursor = nextCursor || '';

                return [
                    ...new Set(
                        slice.content
                            .map(track => track.videoId)
                            .filter(Boolean)
                    )
                ];
            })
            .catch(error => {
                nextBatchPromise = null;
                throw error;
            });

        return nextBatchPromise;
    }

    function playNextBatch(player) {
        requestNextBatch()
            .then(nextVideoIds => {
                if(nextVideoIds.length === 0) {
                    statusElement.textContent =
                        '모든 YouTube곡을 재생했습니다.';

                    return;
                }

                videoIds = nextVideoIds;
                nextBatchPromise = null;

                player.loadPlaylist(
                    videoIds,
                    0, 0
                );

                statusElement.textContent = `다음 ${videoIds.length}곡을 불러왔습니다.`;
            })
            .catch(() => {
                statusElement.textContent =
                    '다음 재생목록을 불러오지 못했습니다.';
            });
    }

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
                    const player = event.target;

                    const currentIndex = player.getPlaylistIndex();

                    if(currentIndex >= 0) {
                        lastKnownIndex = currentIndex;

                        statusElement.textContent =
                            `${currentIndex + 1} / ${videoIds.length}곡 재생 중`;
                    }

                    if(nextCursor && lastKnownIndex >= videoIds.length - 2) {
                        requestNextBatch().catch(() => {
                            statusElement.textContent = '다음 재생목록을 불러오지 못했습니다.';
                        });
                    }

                    const finishedCurrentBatch = event.data === YT.PlayerState.ENDED
                        && lastKnownIndex === videoIds.length -1;

                    if(finishedCurrentBatch) {
                        playNextBatch(player);
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