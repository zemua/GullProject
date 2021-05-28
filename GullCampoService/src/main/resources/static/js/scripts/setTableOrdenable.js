import tableDragger from 'table-dragger'
var el = document.getElementById('tabla-ordenable');
var dragger = tableDragger(el, {
  mode: 'row',
  dragHandler: '.handle',
  onlyBody: true,
  animation: 300
});
dragger.on('drop',function(from, to, el, mode){
  console(from);
  console(to);
});