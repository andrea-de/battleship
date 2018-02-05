// Navigation //
////////////////

var loggedIn = false;
var RecentURL;
$('.game').toggle(false);
$('.loggedIn').toggle(false);
$('instructions').toggle(false);

function loginTasks() {
  $('.loggedIn').toggle(true);
  $('.notLoggedIn').toggle(false);
  getData();
  clearFields();
  getRecord();
  getProfile();
  if (loggedIn) {
    loggedIn = false;
  } else {
    loggedIn = true;
  }
}

function getProfile() {
  $.get("/api/profile")
    .done(function (data) {
      if (data != null) {
        var innerh4 = '<h4>' + data[0] + '</h4>';
        var innerP1 = '<p> Email: ' + data[1] + '</p><br>';
        var innerP2 = '<p>' + data[2] + ' Wins - ' + data[3] + ' Losses</p>';
        $('.profileInfo').html(innerh4 + innerP1 + innerP2);
      }
    }).fail(function (err) {
      console.log(err);
    });
}

function goToGame(url) {
  $('.home').toggle(false);
  $('.game').toggle(true);
  RecentURL = url;
  getGame(url);
}

$('.topBar button').click(function () {
  window.location.href = ('main.html');
  //window.location.replace('main.html');
});

// Sign in or Sign up
$('#formDiv>span:nth-of-type(1)').toggleClass('big');
$('#formDiv div:nth-of-type(2)').toggle();
$('#formDiv span').click(function () {
  $('#formDiv>span:nth-of-type(1)').toggleClass('big');
  $('#formDiv>span:nth-of-type(2)').toggleClass('big');
  $('#formDiv>div:nth-of-type(1)').toggle();
  $('#formDiv>div:nth-of-type(2)').toggle();
});


// Populate Tables //
/////////////////////

$(document).ready(function () {
  loginStatus();
});

function getData() {
  $.get("/api/games")
    .done(function (json) {
      var data = json;
      //console.log(data);
      populate(data);
      //showOutput(JSON.stringify(data, null, 2));
    }).fail(function () {
      console.log("games asset failed to load");
    });
}

function populate(data) {
  myGamesInfo.innerHTML = '';
  gamesInfo.innerHTML = '';
  $(data).each(function (i, v) {
    //makeGameInstance(v);
    fillGamesTable(v);
  });
}

function fillGamesTable(game) {
  var tr = document.createElement('tr');

  /// Date ///
  var dateCell = document.createElement('td');
  dateCell.innerText = game.createdate.split(' ')[0];

  /// Player 1 ///
  var player1Cell = document.createElement('td');
  $(player1Cell).text(game.gamePlayers[0].player.email);

  /// Player 2 ///
  /// Or Join  ///
  var player2Cell = document.createElement('td');
  if (game.gamePlayers[1] != null) {
    $(player2Cell).text(game.gamePlayers[1].player.email);
  } else if (game.link.split('/')[3] != 'viewer') {
    $(player2Cell).text('waiting for opponent');
  } else if (loggedIn) {
    let joinButton = document.createElement('button');
    joinButton.innerText = "Join";
    $(joinButton).click(function (v) {
      var gameNumber = game.link.split('/')[2];
      join = gameNumber;
      placeShips();
    });
    $(player2Cell).append(joinButton);
  }

  /// Link to Game ///
  let linkCell = document.createElement('td');
  let button = document.createElement('button');
  if (game.link.split('/')[3] == 'viewer') {
    button.innerText = "Watch";
  } else {
    button.innerText = "Play";
  }
  $(button).click(function () {
    goToGame(game.link);
    if (game.link.split('/')[3] != 'viewer') {
      myGamePlayerNumber = game.link.split('/')[3];
      myGame = game.link.split('/')[2];
    }
  })
  $(linkCell).append(button);

  /// Put Cells in Row ///
  $(tr).append(dateCell).append(player1Cell).append(player2Cell).append(linkCell);

  /// Distribute rows into correct table ///
  if (game.complete) {
    // put in finished games table
  } else if (game.link.split('/')[3] != 'viewer') {
    $('#myGamesInfo').append(tr);
  } else if (game.link.split('/')[3] == 'viewer') {
    $('#gamesInfo').append(tr);
  }
}

///  Sign In   ///
///  Sign Out  ///
///  Sign Up   ///
//////////////////

$('#emailInput').bind('keyup', function () {
  checkEmail();
});

function login() {
  //evt.preventDefault();
  //var form = evt.target.form;
  $.post("api/login", {
      email: document.forms.namedItem('login-form')["email"].value,
      pwd: document.forms.namedItem('login-form')["pwd"].value
    })
    .done(function () {
      console.log('logged in');
      //console.log() USER AUTH
      //window.location.href = '/game.html';
      loginTasks();
    }).fail(function () {
      console.log('login unsuccessful');
    });
}

function logout() {
  //evt.preventDefault();
  $.post("api/logout", {})
    .done(function () {
      window.location.href = ('main.html');
      /*
      loginTasks();
      getData();
      */
    }).fail(function () {
      console.log('logout unsuccessful');
    });
}

function checkEmail() {
  //var emailInput = $('#emailInput');
  if (emailInput.value == "") {} else {
    $.post({
      url: "api/checkEmail",
      data: document.forms.namedItem('signup-form')["email"].value,
      contentType: "application/json",
      dataType: "text"
    }).done(function (response, status, jqXHR) {
      console.log(jqXHR.responseText);
      $('#eInfo').text(jqXHR.responseText);
      newEmail();
    }).fail(function (jqXHR, status, httpError) {
      //$('#eInfo').text(jqXHR.responseText);
    });
  }
}

function newEmail() {
  if (eInfo.innerText == "email available") {
    $('#eInfo').removeClass('bad').addClass('good');
    $('#signupButton').removeAttr("disabled");
  } else {
    $('#eInfo').removeClass('good').addClass('bad');
    $('#signupButton').attr('disabled', 'enabled');
  }
}

function signup() {
  let a = 0;
  let b = 0;
  a += (document.forms.namedItem('signup-form')["fName"].value != "") ? 1 : 0;
  b += 1;
  a += (document.forms.namedItem('signup-form')["lName"].value != "") ? 1 : 0;
  b += 1;
  a += (document.forms.namedItem('signup-form')["email"].value != "") ? 1 : 0;
  b += 1;
  a += (document.forms.namedItem('signup-form')["pwd"].value != "") ? 1 : 0;
  b += 1;
  if (a == b) {
    //evt.preventDefault();
    $.post({
      url: "api/newplayer",
      data: JSON.stringify({
        firstName: document.forms.namedItem('signup-form')["fName"].value,
        lastName: document.forms.namedItem('signup-form')["lName"].value,
        email: document.forms.namedItem('signup-form')["email"].value,
        password: document.forms.namedItem('signup-form')["pwd"].value
      }),
      dataType: "text",
      contentType: "application/json"
    }).done(function () {
      console.log('you are signed up');
      document.forms.namedItem('login-form')["email"].value = document.forms.namedItem('signup-form')["email"].value;
      document.forms.namedItem('login-form')["pwd"].value = document.forms.namedItem('signup-form')["pwd"].value;
      login();
      clearFields();

    }).fail(function () {
      alert('you are not signed up');
    });
  } else {
    alert('please fill all fields');
  }
}

function loginStatus() {
  $.get("/api/status")
    .done(function (data, status, xhr) {
      if (xhr.responseText == 'Logged In') {
        loginTasks();
      } else {
        getData();
      }
      //showOutput(JSON.stringify(data, null, 2));
    }).fail(function () {
      console.log("could not connect to server");
    });
}

function clearFields() {
  $('#formDiv input').each(function (i, v) {
    if (!(v.value == "Sign up" || v.value == "Log in")) {
      v.value = '';
    }
  });
}

/////////////// Game View /////////////////////

// Make Boards //
/////////////////

var letters = ['A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J'];

var enemyBoard = document.querySelector('#gameBoardDiv1 table');

var playerBoard = document.querySelector('#gameBoardDiv2 table');

makeBoard(enemyBoard);
makeBoard(playerBoard);

function makeBoard(table) {
  for (let i = 0; i < 11; i++) {
    var row = document.createElement('tr');
    if (i == 0) {
      topRowCells(row);
    } else {
      for (let j = 0; j < 11; j++) {
        normalRowCells(row, i, j);
      }
    }
    table.appendChild(row);
  }
}

function topRowCells(row) {
  for (let i = 0; i < 11; i++) {
    var cell = document.createElement('td');
    if (i == 0) {} else {
      cell.innerText = i;
    }
    row.appendChild(cell);
  }
}

function normalRowCells(row, i, j) {
  if (j == 0) {
    var cell = document.createElement('td');
    cell.innerText = letters[i - 1];
    row.appendChild(cell);
  } else {
    var cell = document.createElement('td');
    cell.setAttribute("data-coordinate", letters[i - 1] + j);
    //cell.innerText = letters[i - 1] + j;
    //cellStuff(cell, spots);
    row.appendChild(cell);
  }
}

// Game Request //
//////////////////

function getGame(url) {
  $.get(url).done(function (data) {
    fillBoards(data);
  }).fail(function () {
    console.log("game asset failed to load");
  });
}

// Fill Board(s) //
///////////////////

function fillBoards(data) {
  var playerTurn = true;
  let board = playerBoard;
  let h4reference = 1;
  if (Object.keys(data).length == 1) {
    $('.gameDivs h4')[0].innerText = '...waiting for opponent...';
  }
  for (player in data) {
    $('.gameDivs h4')[h4reference].innerText = player;
    h4reference = 0;
    fillBoard(board, data[player].Hits, data[player].Ships, data[player].Salvos)
    board = enemyBoard;
    if (data[player].Turn) {
      enterSalvoCoordinates();
      instructions('fire');
      //return;
    } else if (data[player].Ships != undefined) {
      instructions('alone');
    } else if (!playerTurn) {
      instructions('wait');
    }
  }
}

function fillBoard(board, hits, ships, salvos) {
  if (ships != null) {
    putShips(board, ships);
  }
  if (salvos != null) {
    putSalvos(salvos);
  }
  putHits(board, hits);
}

function putSalvos(salvos) {
  let salvoCoordinates = [];
  for (salvo in salvos) {
    for (coordinate of salvos[salvo]) {
      salvoCoordinates.push(coordinate);
    }
  }
  for (coordinate of salvoCoordinates) {
    $(enemyBoard)
      .find("[data-coordinate='" + coordinate + "']")
      .text('x');
  }
}

function putHits(board, hits) {
  for (hit in hits) {
    $(board)
      .find("[data-coordinate='" + hit + "']")
      .addClass('hit')
      .text(hits[hit]);
  }
}

function putShips(board, allShips) {
  let shipCoordinates = [];
  for (ship in allShips) {
    for (coordinate of allShips[ship]) {
      shipCoordinates.push(coordinate);
    }
  }
  for (coordinate of shipCoordinates) {
    $(board)
      .find("[data-coordinate='" + coordinate + "']")
      .addClass('ship');
  }
}

// Fire !!! //
//////////////

function instructions(status) {
  let ins = $('.instructions').toggle(true);
  if (status == 'place') {
    //console.log(ins.innerText);
    ins.text('Place all ships on grid and Press Start. Spacebar key to rotate');
  } else if (status == 'alone') {
    ins.text('waiting for opponent to join game');
  } else if (status == 'fire') {
    ins.text('Fire three shots into enemy grid');
  } else if (status == 'wait') {
    ins.text('Waiting for enemy shots');
  } else {
    //Endgame

  }
}

$('#fire').toggle();

var nextSalvo = [];
var myGamePlayerNumber;
var myGame;

function enterSalvoCoordinates() {
  $('#gameBoardDiv1 td[data-coordinate]').mouseenter(function () {
    if ($(this).text() != '') {
      $(this).addClass('badShot');
    } else {
      $(this).addClass('goodShot');
    }
  }).mouseleave(function () {
    $('#gameBoardDiv1 td[data-coordinate]').removeClass('badShot goodShot');
  }).click(function () {
    if ($(this).hasClass('goodShot')) {
      $('#gameBoardDiv1 td[data-coordinate]').removeClass('badShot goodShot');
      $(this).addClass('readyToFire');
      nextSalvo.push($(this).attr('data-coordinate'));
    }
    if ($('.readyToFire').length == 3) {
      // ready to fire
      $('#gameBoardDiv1 td[data-coordinate]').off();
      $('#fire').toggle(true).click(function () {
        // change 1 to GP
        fire(myGamePlayerNumber, nextSalvo);
        nextSalvo = [];
        $('#fire').toggle(false);
        $('#gameBoardDiv1 td[data-coordinate]').removeClass('readyToFire');
      });
    }
  });
}

function fire(gp, coordinates) {
  $.post({
    url: "api/salvo/" + gp,
    data: JSON.stringify(coordinates),
    dataType: "text",
    contentType: "application/json"
    //contentType: "application/json"
  }).done(function (res) {
    console.log(res);

    if (res == "You Won") {
      $('.instructions').text(res)
      $('#fire').toggle(false);
    }
    getGame(RecentURL);
  }).fail(function (err) {
    console.log(err);
  });
}

// Place Ships //
/////////////////

$('#start').toggle();
$('#shipPlacement').toggle();

function placeShips() {
  $('.home').toggle(false);
  $('.game').toggle(true);
  instructions('place');
  $('#gameBoardDiv1').toggle();
  $('#shipPlacement').toggle();
  $('#start').toggle();
  //joinGame(gameNumber); // move to start function(join/create)
}


$('div[data-length]').click(function () {
  //console.log($(this).attr('data-length'));
  place($(this).attr('data-length'));
});

function place(shipSize) {
  var shipCells;
  var horizontal = true
  $(document).keyup(function (e) {
    if (e.keyCode == 32) {
      horizontal = !horizontal;
    }
  });
  $('td[data-coordinate]').each(function () {
    $(this).mouseenter(function () {
      // Find cells for ship placement
      if (horizontal) {
        if (shipSize == 2) {
          shipCells = [$(this), $(this).next(), ]
        }
        if (shipSize == 3) {
          shipCells = [$(this), $(this).next(), $(this).next().next()]
        }
        if (shipSize == 4) {
          shipCells = [$(this), $(this).next(), $(this).next().next(), $(this).next().next().next()]
        }
        if (shipSize == 5) {
          shipCells = [$(this), $(this).next(), $(this).next().next(), $(this).next().next().next(), $(this).next().next().next().next()]
        }
      } else {
        var originCoor = $(this).attr('data-coordinate');
        var coor = [];
        coor[0] = originCoor.split('')[0];
        if (originCoor.split('').length == 2) {
          coor[1] = originCoor.split('')[1];
        } else {
          coor[1] = originCoor.split('')[1] + originCoor.split('')[2];
        }

        function nextChar(char, n) {
          return String.fromCharCode(originCoor[0].charCodeAt(0) + n);
        }
        if (shipSize == 2) {
          shipCells = [$(this),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']')
          ]
        }
        if (shipSize == 3) {
          shipCells = [$(this),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']')
          ]
        }
        if (shipSize == 4) {
          shipCells = [$(this),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 3) + coor[1] + ']')
          ]
        }
        if (shipSize == 5) {
          shipCells = [$(this),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 3) + coor[1] + ']'),
            $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 4) + coor[1] + ']')
          ]
        }
      }
      // Dummy variable to see cells are in grid and unoccupied
      var placeCheck = 0;
      for (cell of shipCells) {
        placeCheck = placeCheck + cell.length;
        if ($(cell).hasClass('ship')) {
          placeCheck = 0;
        }
      }
      // Mark Cells to Place or Not
      if (placeCheck == shipSize) {
        $(shipCells).each(function (i, v) {
          v.addClass('placement');
        });
      } else {
        $(shipCells).each(function (i, v) {
          v.addClass('badPlacement');
        });
      }
    }).mouseleave(function () {
      $('td[data-coordinate]').removeClass('placement').removeClass('badPlacement');
    }).click(function () {
      if ($('.placement').length == shipSize) {
        // Removes Legend Ships
        if (shipSize == 3) {
          if ($('div[data-length=3]').first().is(':visible')) {
            $('div[data-length=3]').first().toggle();
          } else {
            $('div[data-length=3]').last().toggle();
          }
        } else {
          $('div[data-length=' + shipSize + ']').first().toggle();
        }
        $('.placement').addClass('ship');
        // Add to ships array
        let coordinates = [];
        $(shipCells).each(function () {
          coordinates.push($(this).attr('data-coordinate'))
        });
        if (shipSize == 5) {
          shipsPlacement['Carrier'] = coordinates;
        } else if (shipSize == 4) {
          shipsPlacement['Battleship'] = coordinates;
        } else if (shipSize == 2) {
          shipsPlacement['Destroyer'] = coordinates;
        } else if (!("Cruiser" in shipsPlacement)) {
          shipsPlacement['Cruiser'] = coordinates;
        } else {
          shipsPlacement['Submarine'] = coordinates;
        }
      }
      $('td[data-coordinate]').removeClass('placement').removeClass('badPlacement').off();
    });
  });
}

var shipsPlacement = {
  /*ship1: ["A2", "B2", "C2", "D2", "E2"],
  ship2: ["E6", "E7", "E8", "E9", "E10"],
  ship3: ["F4", "G4", "H4"]*/
};

var create = false;
var join;

$('#start').click(function () {
  if (Object.keys(shipsPlacement).length == 5) {
    $('#shipPlacement').toggle();
    $('#gameBoardDiv1').toggle();
    if (create) {
      createGame();
      create = false;
      instructions('alone');
    } else {
      joinGame(join);
      join = null;
      instructions('fire');
    }
  } else {
    alert('invalid ship placement');
  }
});

// Join Game //
///////////////

function joinGame(gameNumber) {
  $.post({
    url: "api/join/" + gameNumber,
    data: JSON.stringify(shipsPlacement),
    dataType: "text",
    contentType: "application/json"
  }).done(function () {
    console.log('game joined');
  }).fail(function () {
    console.log('game not joined');
  });
}

// Create Game //
/////////////////

$('#createGame').click(function () {
  if (loggedIn) {
    placeShips();
    create = true;
  }
})

function createGame() {
  $.post({
    url: "api/newgame",
    data: JSON.stringify(shipsPlacement),
    dataType: "text",
    contentType: "application/json"
  }).done(function () {
    console.log('game started');
    create = false;
  }).fail(function () {
    console.log('game not started');
  });
}

// Scores //
////////////

function getMyInfo() {
  $.get('/api/profile').done(function (data) {
    profile(data);
  }).fail(function (err) {
    console.log(err);
  });
}

function profile(data) {
  console.log(data);
}

function getRecord() {
  $.get('/api/record').done(function (data) {
    fillRecord(data);
  }).fail(function (err) {
    console.log(err);
  });
}

function fillRecord(data) {
  for (player in data) {
    let markup = '<tr><td>' + player + '</td><td>' + data[player].wins + '</td><td>' + data[player].losses + '</td></tr>';
    $('#record').append(markup);
  }
  sortTable(1, 'd');
  for (let i = 3, len = $('#record tr').length; i < len; i++) {
    // Always removing fourth row until there is no fourth row
    //let j = 2;
    //$('#record tr')[j].remove();
  }
}

//borrowed
function sortTable(column, direction) {
  column = column || 0;
  var tbl = document.getElementById("record");
  var store = [];
  for (var i = 0, len = tbl.rows.length; i < len; i++) {
    var row = tbl.rows[i];
    var sortnr = row.cells[column].textContent || row.cells[column].innerText;
    if (!isNaN(sortnr)) {
      sortnr = parseFloat(sortnr);
    }
    store.push([sortnr, row]);
  }
  store.sort(function (x, y) {
    return x[0] > y[0] ? 1 : x[0] < y[0] ? -1 : 0;
  });
  // look for "d" (descending) in the direction to switch sort direction
  if (direction && /d/.test(direction)) {
    store.reverse();
  }
  for (var i = 0, len = store.length; i < len; i++) {
    tbl.appendChild(store[i][1]);
  }
  store = null;
}

// Dev //
/////////

function loadTestData() {
  $.get('/dev/loadStarterData').done(function (res) {
    console.log(res);
  }).fail(function (err) {
    console.log(err);
  });
}

function getInstruction(gp) {
  $.get('/api/getInstruction/' + gp).done(function (res) {
    console.log(res);
  }).fail(function (err) {
    console.log(err);
  });
}
