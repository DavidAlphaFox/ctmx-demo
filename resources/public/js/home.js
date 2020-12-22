$(function () {
  $('[data-toggle="tooltip"]').tooltip();
})

document.body.addEventListener('htmx:beforeRequest', () => {
  $('[data-toggle="tooltip"]').tooltip('dispose');
})

document.body.addEventListener('htmx:afterSettle', () => {
  $('[data-toggle="tooltip"]').tooltip();
})
