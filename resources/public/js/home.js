$(function () {
  $('[data-toggle="tooltip"]').tooltip();
  htmx.config.defaultSettleDelay = 0;
})

document.body.addEventListener('htmx:beforeRequest', () => {
  $('[data-toggle="tooltip"]').tooltip('dispose');
})

document.body.addEventListener('htmx:afterSettle', () => {
  $('[data-toggle="tooltip"]').tooltip();
})
