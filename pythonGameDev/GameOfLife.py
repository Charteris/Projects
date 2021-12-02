# Develop a cellular automata
from random import random
from typing import List, Tuple
from turtle import Turtle, Screen

# Randomise to a given percentage
def randPercent(percent: float) -> int:
    return int(random() > percent)

# Define cell class
class cell:
    # Initialise cell
    def __init__(self, x: int, y: int, active: bool) -> None:
        self.cell = Turtle()
        self.cell.penup()
        self.cell.setposition(x, y)
        self.cell.hideturtle()
        self.cell.color("black")

        # Set cell state
        if active:
            self.draw()
        self.state = active

    # Draw cell
    def draw(self) -> None:
        self.cell.pendown()
        self.cell.begin_fill()
        for _ in range(4):
            self.cell.forward(10)
            self.cell.left(90)
        self.cell.end_fill()
        self.cell.penup()

    # Update state
    def update(self, active: bool) -> None:
        if self.state != active:
            if not active:
                self.cell.clear()
            else:
                self.draw()
            self.state = active

# Returns a new list of binary strings according to cellular automata rules
def gameOfLife(previousState: List[str], width: int, height: int) -> List[str]:
    ''' Rules state (using Moore's neighbourhood):
        <2 live neighbours = death
        2-3 live neighbours = remains alive
        >3 live neighbours = death
        =3 live neighbours = becomes alive '''

    # Copy new string
    newState = previousState.copy()
    for j, cellRow in enumerate(previousState):
        for i, evalCell in enumerate(cellRow):
            # Check for number of live neighbours
            liveNeighbours = 0
            # Orthogonal checks
            liveNeighbours += (i > 0 and cellRow[i - 1] == 1)
            liveNeighbours += (i < (width - 1) and cellRow[i + 1] == 1)
            liveNeighbours += (j > 0 and previousState[j - 1][i] == 1)
            liveNeighbours += (j < (height - 1) and previousState[j + 1][i] == 1)
            # Diagonal checks
            liveNeighbours += (i > 0 and j > 0 and previousState[j - 1][i - 1] == 1)
            liveNeighbours += (i < (width - 1) and j > 0 and previousState[j - 1][i + 1] == 1)
            liveNeighbours += (i > 0 and j < (height - 1) and previousState[j + 1][i - 1] == 1)
            liveNeighbours += (i < (width - 1) and j < (height - 1) and previousState[j + 1][i + 1] == 1)

            # If alive, check if neighbours kill it
            if evalCell == 1:
                newState[j][i] = int(liveNeighbours >= 2 and liveNeighbours <= 3)
            elif liveNeighbours == 3:
                newState[j][i] = 1
    return newState

# Exits window safely
def exitSafely() -> None:
    global running
    running = False

# Reset cellular automata
def reset() -> None:
    global states, cells
    states = [[randPercent(0.9) for _ in range(w)] for _ in range(h)]
    # Update actual cells
    for y, row in enumerate(states):
        for x, char in enumerate(row):
            cells[y][x].update(char == 1)

# Setup screen
def setupGOL() -> Tuple[Screen, List[str], List[List[cell]]]:
    screen = Screen()
    screen.width = 640
    screen.height = 640
    screen.setup(screen.width, screen.height)
    screen.bgcolor("dim gray")
    screen.title("Cellular Automata")
    screen.tracer(0)

    # Set interrupts
    screen.listen()
    screen.onkeypress(exitSafely, "q")
    screen.onkeypress(reset, "r")

    # Generate cells (64 x 64 cells)
    w, h = int(screen.width / 10), int(screen.height / 10)
    states = [[randPercent(0.9) for _ in range(w)] for _ in range(h)]
    cells = [[cell((x - w//2) * 10, (y - h//2) * 10, states[x][y] == 1) for x in range(w)] for y in range(h)]
    return screen, states, cells

# Main loop
running = True
screen, states, cells = setupGOL()
w, h = int(screen.width / 10), int(screen.height / 10)

# Loop
while running:
    # Update cells
    states = gameOfLife(states, w, h)

    # Update actual cells
    for y, row in enumerate(states):
        for x, char in enumerate(row):
            cells[y][x].update(char == 1)
    screen.update()
screen.bye()