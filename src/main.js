import { PanoPlugin, viewComponent } from '@panomc/sdk';
import { derived } from 'svelte/store';
import { _ as i18n } from '@panomc/sdk/utils/language';
import ApiUtil from '@panomc/sdk/utils/api';
import { showToast } from '@panomc/sdk/toasts';

const pluginId = 'pano-plugin-link-redirect';

// this is to render plugin translations
export const _ = derived(i18n, ($_fn) => {
  return (key, options) => $_fn(`plugins.${pluginId}.${key}`, options);
});


export default class PanoLinkRedirectPlugin extends PanoPlugin {
  async onLoad() {
    const pano = this.pano;

    if (pano.isPanel) {
      // Register Panel Page
      pano.ui.page.register({
        path: '/link-redirects',
        component: viewComponent(() => import('./panel/LinkRedirectsPage.svelte')),
        permission: `pano.plugin.${pluginId}.manage.redirects`,
      });

      // Add to Sidebar
      pano.ui.nav.site.editNavLinks((navigationItems) => {
        const redirectLink = {
          href: '/link-redirects',
          icon: 'fas fa-link',
          text: `plugins.${pluginId}.pages.redirects.title`,
          startsWith: false,
          permission: `pano.plugin.${pluginId}.manage.redirects`,
        };

        const postIndex = navigationItems.findIndex((item) => item.href === '/posts');
        if (postIndex !== -1) {
          navigationItems.splice(postIndex + 1, 0, redirectLink);
        } else {
          navigationItems.push(redirectLink);
        }

        return navigationItems;
      });
    } else {
      // Theme Redirection Logic
      const redirectPageComponent = viewComponent(() => import('./theme/RedirectPage.svelte'));

      pano.ui.app.onLoad(async (data, event) => {
        // Fetch active redirects to register their routes
        try {
          const res = await ApiUtil.get({
            path: '/api/link-redirects',
            request: event
          });

          if (res && Array.isArray(res.redirects)) {
            res.redirects.forEach((redirect) => {
              // Register dynamic route for each redirect
              pano.ui.page.register({
                path: redirect.path,
                component: redirectPageComponent,
                loginRequired: redirect.requireLogin,
                permission: redirect.requirePermission ? redirect.permissionNode : null,
                resetLayout: true, // Always reset layout for redirects as per user request
              });

              // Add to Theme Navigation if enabled
              if (redirect.showInNavigation && pano.ui.nav.site.editNavLinks) {
                pano.ui.nav.site.editNavLinks((navItems) => {
                  // Check if already exists to avoid duplicates
                  if (!navItems.find((n) => n.href === redirect.path)) {
                    navItems.push({
                      href: redirect.path,
                      text: redirect.title, // Literal text
                      target: redirect.openInNewTab ? '_blank' : '_self',
                      startsWith: false,
                      loginRequired: redirect.requireLogin,
                      permission: redirect.requirePermission ? redirect.permissionNode : null,
                    });
                  }
                  return navItems;
                });
              }
            });
          }
        } catch (e) {
          console.error('[LinkRedirectPlugin] Failed to fetch redirects for route registration', e);
        }
      });
    }
  }

  onContextUpdate(ctx) { }

  onUnload() { }
}
