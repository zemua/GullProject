const rows = {
  len: 10,
  1: {
    cells: {
      0: { text: 'testingtesttestetst' },
      2: { text: 'testing' },
    },
  },
  2: {
    cells: {
      0: { text: 'render' },
      1: { text: 'Hello' },
      2: { text: 'haha' },
    }
  },
  8: {
    cells: {
      8: { text: 'border test' },
    }
  }
};

// x_spreadsheet.locale('zh-cn');
var xs = x_spreadsheet('#consultas-spreadsheet', { showToolbar: false, showGrid: true })
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
      len: 10,
      2: { width: 200 },
    },
    rows,
  }]);

xs.on('cell-selected', (cell, ri, ci) => {
  console.log('cell:', cell, ', ri:', ri, ', ci:', ci);
}).on('cell-edited', (text, ri, ci) => {
  console.log('text:', text, ', ri: ', ri, ', ci:', ci);
});
