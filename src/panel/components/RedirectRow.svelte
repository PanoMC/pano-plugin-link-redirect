<script>
  import { _ } from '../../main';
  import tooltip from '@panomc/sdk/utils/tooltip';
  import { showToast } from '@panomc/sdk/toasts';
  import { copy } from '@panomc/sdk/utils/text';
  
  export let redirect;
  export let onEditClick;
  export let onDeleteClick;

  function copyLink() {
    const url = new URL(window.location.origin + redirect.path);
    copy(url.toString());
    showToast('plugins.pano-plugin-link-redirects.pages.redirects.toasts.copy-success');
  }
</script>

<tr>
  <th scope="row" class="align-middle text-center" style="width: 60px;">
    <div class="dropdown position-static">
      <button
        type="button"
        class="btn btn-link"
        aria-expanded="false"
        aria-haspopup="true"
        data-bs-toggle="dropdown"
        use:tooltip={[$_('pages.redirects.actions.label')]}
        aria-label={$_('pages.redirects.actions.label')}>
        <span class="fas fa-ellipsis-v"></span>
      </button>
      <div class="dropdown-menu dropdown-menu-start animate__animated animate__fadeIn">
        <button
          type="button"
          class="dropdown-item"
          on:click={() => onEditClick(redirect.id)}>
          <span>
            <i class="fas fa-edit me-2"></i>
            {$_('common.edit')}
          </span>
        </button>
        <button
          type="button"
          class="dropdown-item"
          on:click={copyLink}>
          <span>
            <i class="fas fa-copy me-2"></i>
            {$_('pages.redirects.actions.copy-link')}
          </span>
        </button>
        <button
          type="button"
          class="dropdown-item"
          on:click={() => onDeleteClick(redirect.id)}>
          <i class="fas fa-trash me-2"></i>
          <span> {$_('common.delete')} </span>
        </button>
      </div>
    </div>
  </th>
  <td class="align-middle">
    <code>{redirect.id}</code>
  </td>
  <td class="align-middle">
   <div class="text-truncate">
      <button
        type="button"
        title={redirect.title}
        use:tooltip={[$_('common.edit')]}
        aria-label={$_('common.edit')}
        on:click={() => onEditClick(redirect.id)}
        class="btn btn-link p-0 text-start text-decoration-none w-100 text-truncate">
        {redirect.title}
      </button>
      {#if redirect.useCustomPage}
        <span class="badge bg-info-subtle text-info border border-info-subtle ms-1" style="font-size: 10px;">CUSTOM</span>
      {/if}
    </div>
  </td>
  <td class="align-middle">
    <div class="d-flex align-items-center gap-2">
      <span class="badge bg-light text-dark font-monospace border">{$_('pages.redirects.fields.path-hint-short') || '/'}</span>
      <code class="text-primary">{redirect.path}</code>
    </div>
  </td>
  <td class="align-middle">
    <div class="text-truncate" style="max-width: 250px;">
      <a href={redirect.targetUrl} target="_blank" rel="noopener noreferrer" class="text-decoration-none">
        {redirect.targetUrl}
      </a>
    </div>
  </td>
  <td class="align-middle text-nowrap">
    <span class="badge bg-light text-dark font-monospace border">
        {redirect.delay}s
    </span>
  </td>
</tr>
