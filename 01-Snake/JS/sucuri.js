

const CANVAS_BORDER_COLOUR = 'black';
const CANVAS_BACKGROUND_COLOUR = "lightgreen";
const SNAKE_COLOUR = "yellow";
const SNAKE_BORDER_COLOUR = "green";
const FOOD_COLOUR = "red";
const FOOD_BORDER_COLOUR = "red"


let GAME_SPEED =100;
let MORE_SPEED = 1;

let snake = [
  {x: 150, y: 150},
  {x: 140, y: 150},
  {x: 130, y: 150},
  {x: 120, y: 150},
  {x: 110, y: 150}
]



let score = 0;
let foodX,foodY;
let dx= 10;

let dy=0;

let changingDirection = false;

const gameCanvas = document.getElementById("gameCanvas");
const ctx = gameCanvas.getContext("2d");
MAIN();
createFood();

document.addEventListener("keydown",changeDirection);



function MAIN(){


    if(didGameEnd()) {
        alert(`Seu placar foi de ${score} pontos!`,"Fim de jogo")

        return ;
    } 

    setTimeout(function onTick(){
        changingDirection = false;
        clearCanvas();
        drawFood();
        advanceSnake();
        drawSnake();
        
        MAIN();
    },GAME_SPEED)
}

function clearCanvas(){
    ctx.fillStyle = CANVAS_BACKGROUND_COLOUR;
    ctx.strokeStyle=CANVAS_BORDER_COLOUR;

    ctx.fillRect(0, 0,gameCanvas.width, gameCanvas.height  );
    ctx.strokeRect(0, 0,gameCanvas.width, gameCanvas.height  );
}


function drawFood() {
    ctx.fillStyle = FOOD_COLOUR;
    ctx.strokeStyle = FOOD_BORDER_COLOUR;
    ctx.fillRect(foodX, foodY, 10, 10);
    ctx.strokeRect(foodX, foodY, 10, 10);
  }



 function advanceSnake(){
    const head = {x: snake[0].x + dx, y: snake[0].y + dy};
    snake.unshift(head);
    const didEatFood = snake[0].x === foodX && snake[0].y === foodY;
    if(didEatFood){


        score ++;
        GAME_SPEED -= 10;

        console.log("Game speed invertidade %i",GAME_SPEED);
        
        createFood();
    }
    else
        snake.pop();

}



function didGameEnd(){
    for(let i=4; i < snake.length; i++)
        if(snake[i].x === snake[0].x && snake[i].y === snake[0].y)
            return true;

    const hitLeftWall = snake[0].x < 0;
    const hitRightWall = snake[0].x > gameCanvas.width - 10;
    const hitTopWall = snake[0].y < 0;
    const hitBottomWall = snake[0].y> gameCanvas.height -10;


    return hitBottomWall || hitLeftWall || hitRightWall || hitTopWall;

}



function randomTen(min, max){
    return Math.round((Math.random()*(max-min) +min)/10)*10;
}

function createFood(){
    foodX = randomTen(0, gameCanvas.width -10);
    foodY = randomTen(0, gameCanvas.height -10);

    snake.forEach(function isFoodOnSnake(part){
        const foodIsOnSnake = part.x == foodX && part.y == foodY;
        if(foodIsOnSnake)
            createFood();
    });
}




function drawSnake(){
    snake.forEach(drawSnakePart)
}

 function drawSnakePart(snakePart) {
  
  ctx.fillStyle = SNAKE_COLOUR;
  ctx.strokeStyle = SNAKE_BORDER_COLOUR;

  ctx.fillRect(snakePart.x, snakePart.y, 10, 10);
   ctx.strokeRect(snakePart.x, snakePart.y, 10, 10);
}



function changeDirection(event){

    
    const LEFT_KEY = 37;
    const RIGHT_KEY = 39;
    const UP_KEY = 38;
    const DOWN_KEY = 40;

    if(changingDirection) return ;

    changingDirection = true;

    const keyPressed = event.keyCode;

    const goingUp = dy === -10;
    const goingDown = dy === 10;
    const goingLeft = dx === -10;
    const goingRight = dx === 10;

    if(keyPressed=== LEFT_KEY && !goingRight){
        dx= -10;
        dy=0;
    }
    if(keyPressed=== RIGHT_KEY && !goingLeft){
        dx= 10;
        dy=0;
    }
    if(keyPressed=== UP_KEY && !goingDown){
        dx= 0;
        dy= -10;
    }
    if(keyPressed=== DOWN_KEY && !goingUp){
        dx= 0;
        dy= 10;
    }
}


