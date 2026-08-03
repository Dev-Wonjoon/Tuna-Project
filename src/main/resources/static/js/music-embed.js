const embedProviders = [
    {
        name: 'youtube',

        resolve(url) {
            const videoId = extractYoutubeVideoId(url);

            if(!videoId) {
                return null;
            }

            return {
                title: 'YouTube 동영상 플레이어',
                src: `https://www.youtube-nocookie.com/embed/${videoId}?rel=0`,
                className: 'aspect-video w-full rounded-lg'
            };
        },
    },
    {
        name: 'spotify',

        resolve(rawUrl) {
            const spotify = extractSpotifyContent(rawUrl);

            if(!spotify) {
                return null;
            }

            return {
                title: 'Spotify 음악 플레이어',
                src: `https://open.spotify.com/embed/${spotify.type}/${spotify.id}`,
                className: 'h-[152px] w-full rounded-lg',
            }
        }
    }
];

function extractYoutubeVideoId(rawUrl) {
    if(!rawUrl) {
        return null;
    }

    try {
        const url = new URL(rawUrl.trim());

        if(url.protocol !== 'https:' && url.protocol !== 'http') {
            return null;
        }

        const host = url.hostname
            .toLowerCase()
            .replace(/^www\./, '');

        let videoId = null;

        if(host === 'youtu.be') {
            videoId = url.pathname
                .split('/')
                .filter(Boolean)[0] ?? null;
        } else if(host === 'youtube.com' || host === 'm.youtube.com' || host === 'music.youtube.com') {
            if(url.pathname === '/watch') {
                videoId = url.searchParams.get('v');
            } else {
                const match = url.pathname.match(/^\/(?:shorts|embed|live)\/([^/?#]+)/);

                videoId = match?.[1] ?? null;
            }
        } else if(host === 'youtube-nocookie.com') {
            const match = url.pathname.match(/^\/embed\/([^/?#]+)/);

            videoId = match?.[1] ?? null;
        }

        return /^[A-Za-z0-9_-]{11}$/.test(videoId ?? '') ? videoId : null;
    } catch {
        return null;
    }
}

function extractSpotifyContent(rawUrl) {
    if(!rawUrl) {
        return null;
    }

    try {
        const url = new URL(rawUrl.trim());

        if(url.protocol !== 'https:' || url.hostname !== 'open.spotify.com') {
            return null;
        }
        const match = url.pathname.match(/^\/(track|album|playlist|artist|episode|show)\/([A-Za-z0-9]+)\/?$/);

        if(!match) {
            return null;
        }

        return {
            type: match[1],
            id: match[2],
        };
    } catch {
        return null;
    }
}

document.querySelectorAll('[data-music-embed]')
    .forEach(initializeMusicEmbed);

function initializeMusicEmbed(root) {
    const embed = embedProviders
        .map(provider => provider.resolve(root.dataset.musicUrl))
        .find(Boolean);

    const iframe = root.querySelector('[data-music-embed-frame]');

    if(!embed || !iframe) {
        return;
    }

    iframe.src = embed.src;
    iframe.title = embed.title;
    iframe.className = embed.className;
    root.hidden = false;
}