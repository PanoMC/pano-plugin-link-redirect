<div class="pano-redirect-page" class:has-custom-content={redirect?.useCustomPage} style={redirect?.useCustomPage ? 'display: block;' : ''}>
    {#if redirect && redirect.showIntermediatePage}
        <div class="pano-redirect-overlay" class:has-custom-content={redirect?.useCustomPage}>
            {#if redirect.intermediatePageDesign === 'MINIMAL'}
                <div class="pano-redirect-loader" style="border-width: 3px; width: 32px; height: 32px; margin: 0 1rem 0 0; border-top-color: white; border-right-color: transparent;"></div>
                <div style="font-size: 1.1rem; font-weight: 500;">
                    Redirecting to <b>{hostname}</b>{#if remaining > 0} in <span id="pano-redirect-countdown">{remaining}</span>s{/if}...
                </div>
            {:else if redirect.intermediatePageDesign === 'MODERN'}
                <div class="pano-redirect-card" style="background: #1f2937; color: white;">
                    <div class="pano-redirect-loader" style="border-top-color: #60a5fa; border-right-color: transparent;"></div>
                    <div class="pano-redirect-title" style="color: white;">Redirecting</div>
                    <div class="pano-redirect-url" style="color: #9ca3af;">Taking you to {hostname}</div>
                    <div class="pano-redirect-progress-container" style="background: #374151;">
                        <div id="pano-redirect-progress" class="pano-redirect-progress-bar" style="background: #60a5fa; width: {progress}%"></div>
                    </div>
                    <div style="margin-top: 0.5rem; font-size: 0.8rem; color: #6b7280;">Please wait while we redirect you...</div>
                </div>
            {:else if redirect.intermediatePageDesign === 'CUSTOM'}
                <!-- Custom design: We don't render standard cards. 
                     The overlay will remain if showIntermediatePage is true, 
                     but it will be transparent/blurred according to .has-custom-content styles -->
            {:else}
                <div class="pano-redirect-card">
                    <div class="pano-redirect-loader"></div>
                    <div class="pano-redirect-title">Redirecting...</div>
                    <div class="pano-redirect-url">{redirect.targetUrl}</div>
                    <div class="mt-3">
                        {#if remaining > 0}
                            You will be redirected in <b>{remaining}</b> seconds.
                        {:else}
                            You are being redirected...
                        {/if}
                    </div>
                </div>
            {/if}
        </div>
    {/if}
    
    {#if redirect?.useCustomPage && redirect?.htmlContent}
        <div class="pano-custom-redirect-content">
            {@html redirect.htmlContent}
        </div>
    {/if}
</div>

<style>
  .pano-redirect-page {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .pano-redirect-page:not(.has-custom-content) {
    background: #000;
  }

  .pano-redirect-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0, 0, 0, 0.9);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 999999;
    color: white;
    font-family: system-ui, -apple-system, sans-serif;
    backdrop-filter: blur(5px);
  }

  .pano-redirect-overlay.has-custom-content {
    background: transparent;
    backdrop-filter: none;
    z-index: 10;
  }

  .pano-custom-redirect-content {
    position: relative;
    z-index: 5;
    width: 100%;
    min-height: 100vh;
    color: white;
  }
  .pano-redirect-card {
    background: white;
    color: #333;
    padding: 2.5rem;
    border-radius: 16px;
    box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.25);
    text-align: center;
    max-width: 400px;
    width: 90%;
    animation: pano-scale-in 0.3s ease-out;
  }
  .pano-redirect-title {
    font-size: 1.5rem;
    margin-bottom: 0.5rem;
    font-weight: 700;
    color: #111;
  }
  .pano-redirect-url {
    font-size: 0.9rem;
    color: #666;
    margin-bottom: 1.5rem;
    word-break: break-all;
  }
  .pano-redirect-loader {
    width: 48px;
    height: 48px;
    border: 4px solid #f3f3f3;
    border-top: 4px solid #3b82f6;
    border-radius: 50%;
    animation: pano-spin 1s linear infinite;
    margin: 0 auto 1.5rem;
  }
  .pano-redirect-progress-container {
    height: 6px;
    background: #e5e7eb;
    border-radius: 3px;
    overflow: hidden;
    margin-top: 1.5rem;
    width: 100%;
  }
  .pano-redirect-progress-bar {
    height: 100%;
    background: #3b82f6;
    transition: width 1s linear;
  }
  @keyframes pano-spin {
    0% { transform: rotate(0deg); }
    100% { transform: rotate(360deg); }
  }
  @keyframes pano-scale-in {
    0% { transform: scale(0.9); opacity: 0; }
    100% { transform: scale(1); opacity: 1; }
  }
</style>

<script context="module">
  import ApiUtil from '@panomc/sdk/utils/api';
  import { error, redirect as svelteRedirect } from '@panomc/sdk/svelte';

  export async function load(event) {
    const { url } = event;
    const currentPath = event.url.pathname;

    const res = await ApiUtil.get({
      path: `/api/link-redirects/check?path=${currentPath}`,
      request: event
    });

    if (!res || res.error || (res.status && res.status !== 'SUCCESS')) {
      if (res && (res.error === 'NotLoggedIn' || res.errorCode === 'NotLoggedIn')) {
        throw svelteRedirect(302, `/login?redirect=${encodeURIComponent(currentPath)}`);
      }
      return error(404, 'Redirect not found or access denied');
    }

    // Optimization: If no delay, no intermediate page, and no custom content, redirect immediately on server
    if ((res.delay || 0) <= 0 && !res.showIntermediatePage && !res.useCustomPage && !res.openInNewTab) {
        throw svelteRedirect(302, res.targetUrl);
    }

    return { data: { redirect: res } };
  }
</script>

<script>
  import { onMount } from 'svelte';
  export let data;
  
  const redirect = data?.redirect;

  let remaining = redirect?.delay || 0;
  let progress = 0;
  let hostname = '';

  try {
    hostname = new URL(redirect?.targetUrl).hostname;
  } catch (e) {
    hostname = redirect?.targetUrl || '';
  }

  onMount(() => {
    if (!redirect) return;
    
    if (remaining <= 0) {
      performRedirect();
      return;
    }

    const interval = setInterval(() => {
      remaining--;
      progress = ((redirect.delay - remaining) / redirect.delay) * 100;
      if (remaining <= 0) {
        clearInterval(interval);
        performRedirect();
      }
    }, 1000);

    return () => clearInterval(interval);
  });

  function performRedirect() {
    if (!redirect) return;
    if (redirect.openInNewTab) {
      window.open(redirect.targetUrl, '_blank');
    } else {
      window.location.href = redirect.targetUrl;
    }
  }
</script>
