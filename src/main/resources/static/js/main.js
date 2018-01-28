////////////////
// Navigation //
////////////////

/* Use Jquery .hide .show .toggle (toggle class not needed) */
/* Extra request for games - unknown */

var loggedIn = false;
var RecentURL;

function loginTasks() {
    $('#logoutDiv').toggleClass('hide');
    $('#createGame').toggleClass('hide');
    $('#formDiv').toggleClass('hide');
    $('#playerTable').toggleClass('hide');
    getData();
    clearFields();
    if (loggedIn) {
        loggedIn = false;
    } else {
        loggedIn = true;
    }
}

function homePage() {
    showHomeDivs();
}

function setupNewGame() {
    console.log('hi');
    hideHomeDivs();
    $('#shipPlacement').toggleClass('hide');
    $('#gameBoardDiv2').toggleClass('hide');
    create = true;
}

function goToGame(url) {
    hideHomeDivs();
    $('#gameBoardDiv1').removeClass('hide');
    $('#gameBoardDiv2').removeClass('hide');
    RecentURL = url;
    getGame(url);
}

function gameView() {
    hideHomeDivs();
    $('#gameBoardDiv1').toggleClass('hide');
    $('#gameBoardDiv2').toggleClass('hide');
}

function hideHomeDivs() {
    $('.homeDivs').each(function (i, v) {
        $(v).addClass('hide');
    });
}

function showHomeDivs() {
    $('.homeDivs').each(function (i, v) {
        $(v).removeClass('hide');
    });
}

$('#formDiv > span').click(function () {
    $('.signDiv').each(function (i, v) {
        if ($(v).hasClass('hide')) {
            $(v).removeClass('hide');
        } else {
            $(v).addClass('hide');
        }
    });
});

$('#topBar span').click(function () {
    window.location.href = ('main.html');
    //window.location.replace('main.html');
})

/////////////////////
// Populate Tables //
/////////////////////

$(document).ready(function () {
    loginStatus();
    getData();
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
    ////////////
    /// Date ///
    var dateCell = document.createElement('td');
    dateCell.innerText = game.createdate.split(' ')[0];
    ////////////////
    /// Player 1 ///
    var player1Cell = document.createElement('td');
    $(player1Cell).text(game.gamePlayers[0].player.email);
    ////////////////
    /// Player 2 ///
    /// Or Join  ///
    var player2Cell = document.createElement('td');
    if (game.gamePlayers[1] != null) {
        $(player2Cell).text(game.gamePlayers[1].player.email);
    } else if (game.link.split('/')[3] != 'viewer') {
        $(player2Cell).text('Waiting for Opponent');
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
    ////////////////////
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
    ////////////////////////
    /// Put Cells in Row ///
    $(tr).append(dateCell).append(player1Cell).append(player2Cell).append(linkCell);
    //////////////////////////////////////////
    /// Distribute rows into correct table ///
    if (game.link.split('/')[3] != 'viewer') {
        $('#myGamesInfo').append(tr);
    } else if (game.link.split('/')[3] == 'viewer') {
        $('#gamesInfo').append(tr);
    }
}

//////////////////
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
            url: "checkEmail",
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

/////////////////
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

//////////////////
// Game Request //
//////////////////

function getGame(url) {
    $.get(url).done(function (data) {
        fillBoards(data);
        //showOutput(JSON.stringify(data, null, 2));
    }).fail(function () {
        console.log("game asset failed to load");
    });
}

///////////////////
// Fill Board(s) //
///////////////////

function fillBoards(data) {
    let board = playerBoard;
    for (player in data) {
        fillBoard(board, data[player].Hits, data[player].Ships, data[player].Salvos)
        board = enemyBoard;
        if (data[player].Turn) {
            enterSalvoCoordinates();
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

//////////////
// Fire !!! //
//////////////

$('#fire').toggle();

var nextSalvo = [];
var myGamePlayerNumber;
var myGame;
// where is salvo being created??? ///

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
            $('#fire').toggle().click(function () {
                // change 1 to GP
                fire(myGamePlayerNumber, nextSalvo);
                nextSalvo = [];
                $('#gameBoardDiv1 td[data-coordinate]').removeClass('readyToFire');
            });
        }
    });
}

var salvoExample = ["A1", "A2", "B2"]

function fire(gp, coordinates) {
    $.post({
        url: "api/salvo/" + gp,
        data: JSON.stringify(coordinates),
        dataType: "text",
        contentType: "application/json"
        //contentType: "application/json"
    }).done(function () {
        console.log('shots fired!');
        getGame(RecentURL);
    }).fail(function () {
        console.log('misfire!');
    });
}

/////////////////
// Place Ships //
/////////////////

function placeShips() {
    hideHomeDivs();
    $('#shipPlacement').toggleClass('hide');
    $('#gameBoardDiv2').toggleClass('hide');
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
                console.log(coor);

                function nextChar(char, n) {
                    return String.fromCharCode(originCoor[0].charCodeAt(0) + n);
                }

                if (shipSize == 2) {
                    shipCells = [$(this),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']')]
                }
                if (shipSize == 3) {
                    shipCells = [$(this),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']')]
                }
                if (shipSize == 4) {
                    shipCells = [$(this),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 3) + coor[1] + ']')]
                }
                if (shipSize == 5) {
                    shipCells = [$(this),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 1) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 2) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 3) + coor[1] + ']'),
                                 $('#gameBoardDiv2 [data-coordinate=' + nextChar(coor[0], 4) + coor[1] + ']')]
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
                $(shipCells).each(function () {
                    this.addClass('placement');
                });
            } else {
                $(shipCells).each(function () {
                    this.addClass('badPlacement');
                });
            }
        }).mouseleave(function () {
            $('td[data-coordinate]').removeClass('placement').removeClass('badPlacement');
        }).click(function () {
            if ($('.placement').length == shipSize) {
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
    $('#shipPlacement').toggleClass('hide');
    $('#gameBoardDiv1').removeClass('hide');
    if (create) {
        createGame();
        create = false;
    } else {
        joinGame(join);
        join = null;
    }
});

///////////////
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

/////////////////
// Create Game //
/////////////////

$('#createGame').click(function () {
    if (loggedIn) {
        setupNewGame();
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

////////////
// Scores //
////////////
