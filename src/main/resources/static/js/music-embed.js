const embedProviders = [
    {
        name: 'youtube',
        label: 'YouTube',

        resolve(url) {
            const videoId = extractYoutubeVideoId(url);

            if(!videoId) {
                return null;
            }

            return {
                title: 'YouTube 동영상 플레이어',
                src: `https://www.youtube-nocookie.com/embed/${videoId}?autoplay=1&rel=0`,
                thumbnailUrl: `https://i.ytimg.com/vi/${videoId}/hqdefault.jpg`,
                frameClassName: 'aspect-video w-full rounded-lg',
                previewClassName: 'aspect-video',
            };
        },
    },
    {
        name: 'spotify',
        label: 'Spotify',

        resolve(rawUrl) {
            const spotify = extractSpotifyContent(rawUrl);

            if(!spotify) {
                return null;
            }

            return {
                title: 'Spotify 음악 플레이어',
                src: `https://open.spotify.com/embed/${spotify.type}/${spotify.id}`,
                thumbnailUrl: null,
                frameClassName: 'h-[152px] w-full rounded-lg',
                previewClassName: 'h-[152px]',
            };
        },
    },
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

document.querySelectorAll('[data-music-embed]').forEach(initializeMusicEmbed);

function initializeMusicEmbed(root) {
    if(root.dataset.musicEmbedInitialized === 'true') {
        return;
    }

    const resolved = embedProviders
        .map(provider => {
            const embed = provider.resolve(root.dataset.musicUrl);

            if(!embed) {
                return null;
            }

            return {
                ...embed,
                providerName: provider.name,
                providerLabel: provider.label
            };
        }).find(Boolean);

    const trigger = root.querySelector(
        '[data-music-embed-trigger]'
    );

    const iframe = root.querySelector(
        '[data-music-embed-frame]'
    );

    const thumbnail = root.querySelector(
        '[data-music-embed-thumbnail]'
    );

    const providerLabel = root.querySelector(
        '[data-music-embed-provider]'
    );

    if(!resolved || !trigger || !iframe) {
        return;
    }

    root.dataset.musicEmbedInitialized = 'true';

    trigger.classList.remove(
        'aspect-video',
        'h-[152px]'
    );

    trigger.classList.add(resolved.previewClassName);

    iframe.title = resolved.title;
    iframe.className = `hidden ${resolved.frameClassName}`;

    trigger.setAttribute('aria-label', `${resolved.providerLabel} 플레이어 불러오기`);

    if(providerLabel) {
        providerLabel.textContent = resolved.providerLabel;
    }

    if(thumbnail && resolved.thumbnailUrl) {
        thumbnail.src = resolved.thumbnailUrl;
        thumbnail.hidden = false;
    }

    root.hidden = false;

    if(resolved.providerName === 'spotify') {
        root.dataset.musicEmbedLoaded = 'true';

        trigger.hidden = true;
        trigger.classList.add('hidden');

        iframe.src = resolved.src;
        iframe.hidden = false;
        iframe.classList.remove('hidden');

        return;
    }

    trigger.addEventListener('click', () => {
        if(root.dataset.musicEmbedLoaded === 'true') {
            return;
        }

        root.dataset.musicEmbedLoaded = 'true';

        iframe.src = resolved.src;

        iframe.hidden = false;
        trigger.classList.add('hidden');

        iframe.hidden = false;
        iframe.classList.remove('hidden');
    }, { once: true });
}