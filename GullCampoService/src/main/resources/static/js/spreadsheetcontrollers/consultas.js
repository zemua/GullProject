var rows;
function gullSetRows(gulldata) {
  console.log('entering gullSetRows');
  rows = new Object();
  rows.len = gulldata.length;
  for (let i=0; i<gulldata.length; i++) {
    rows[ i ] = new Object();
    rows[ i ].cells = new Object();
    rows[i].cells['0'] = new Object();
    rows[i].cells['0'].text = gulldata[ i ].name;
    rows[i].cells['1'] = new Object();
    rows[i].cells['1'].text = gulldata[ i ].status;
    rows[i].cells['2'] = new Object();
    rows[i].cells['2'].text = "/consultas/id/" + gulldata[ i ].id;
  }
  console.log(rows);
};

var xs;
function gullSheetConfigure() {
  // x_spreadsheet.locale('zh-cn');
  xs = x_spreadsheet('#consultas-spreadsheet', { 
      mode: "read",
      showToolbar: false,
      showGrid: true,
      showContextmenu: false
     })
    .loadData([{
      styles: [
        {
          bgcolor: '#f4f5f8',
          textwrap: true,
          color: '#900b09',
          border: {
            top: ['thin', '#0366d6'],
            bottom: ['thin', '#0366d6'],
            right: ['thin', '#0366d6'],
            left: ['thin', '#0366d6'],
          },
        },
      ],
      cols: {
        len: 3,
        2: { width: 300 },
      },
      rows,
    }]);

  xs.on('cell-selected', (cell, ri, ci) => {
    console.log('cell:', cell, ', ri:', ri, ', ci:', ci);
    // redirect to edit this consulta
    if (ci === 2){
      let h = window.location.host;
      let url = "http://" + h + cell.text;
      window.location.href = url;
    }
  }).on('cell-edited', (text, ri, ci) => {
    console.log('text:', text, ', ri: ', ri, ', ci:', ci);
  });
};

$.getJSON("/api/consultas/allforspreadsheet", function (gulldata) {
  console.log(gulldata);
  gullSetRows(gulldata);
  gullSheetConfigure();
});