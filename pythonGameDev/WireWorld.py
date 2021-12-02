# Develop a cellular automata
from random import random
from typing import List, Tuple
from turtle import Turtle, Screen

# Define cell class
class cell:
    # Initialise cell
    def __init__(self, x: int, y: int, k: int) -> None:
        self.cell = Turtle()
        self.cell.penup()
        self.cell.setposition(x, y)
        self.cell.hideturtle()
        self.cell.color("black")

        # Set cell state
        if k > 0:
            self.draw(k)
        self.state = k

    # Draw cell
    def draw(self, k: int) -> None:
        # Set color according to k value
        if k == 1: # Electron head
            self.cell.color("yellow")
        elif k == 2: # Electron tail
            self.cell.color("red")
        elif k == 3: # Wire
            self.cell.color("black")

        # Draw actual cell
        self.cell.pendown()
        self.cell.begin_fill()
        for _ in range(4):
            self.cell.forward(10)
            self.cell.left(90)
        self.cell.end_fill()
        self.cell.penup()

    # Update state
    def update(self, k: int) -> None:
        if self.state != k:
            self.cell.clear()
            if k > 0:
                self.draw(k)
            self.state = k

# Returns a new list of binary strings according to cellular automata rules
def wireWorld(previousState: List[str], width: int, height: int) -> List[str]:
    ''' Rules state (using Moore's neighbourhood):
        state 0 is background
        state 1 is electron head which always turns to an electron tail
        state 2 is an electron tail which always turns to a wire
        state 3 is a wire which remains a wire unless a neighbour is of 1 or 2 (in which case it becomes 1) '''

    # Copy new string
    newState = previousState.copy()
    for j, cellRow in enumerate(previousState):
        for i, evalCell in enumerate(cellRow):
            # Background (remains static)
            if evalCell == 0:
                continue

            # electron head or tail (increments)
            if evalCell == 1 or evalCell == 2:
                newState[j][i] += 1
                continue

            # Wire (becomes electron head if any neighbour is a head or tail)
            check = lambda neighbour: neighbour == 1 or neighbour == 2
            liveNeighbours = 0
            # Orthogonal checks
            liveNeighbours += (i > 0 and check(cellRow[i - 1]))
            liveNeighbours += (i < (width - 1) and check(cellRow[i + 1]))
            liveNeighbours += (j > 0 and check(previousState[j - 1][i]))
            liveNeighbours += (j < (height - 1) and check(previousState[j + 1][i]))
            # Diagonal checks
            liveNeighbours += (i > 0 and j > 0 and check(previousState[j - 1][i - 1]))
            liveNeighbours += (i < (width - 1) and j > 0 and check(previousState[j - 1][i + 1]))
            liveNeighbours += (i > 0 and j < (height - 1) and check(previousState[j + 1][i - 1]))
            liveNeighbours += (i < (width - 1) and j < (height - 1) and check(previousState[j + 1][i + 1]))

            if liveNeighbours > 0:
                newState[j][i] = 1
    return newState

# Exits window safely
def exitSafely() -> None:
    global running
    running = False

# Sets a cell to a wire
def setWire(mx: float, my: float) -> None:
    # Get relevant cell index
    global screen, cells
    ix, iy = int(mx // 10), int(my // 10)
    if cells[ix][iy].state != 3:
        cells[ix][iy].update(3)
    else:
        cells[ix][iy].update(0)

# Sets a cell to an electron head (if already wire)
def trigger(mx: float, my: float) -> None:
    # Get relevant cell index
    global screen
    ix, iy = int(mx // 10), int(my // 10)
    if cells[iy][ix].state != 0:
        cells[iy][ix].update(1)

# Setup screen
def setupWW() -> Tuple[Screen, List[str], List[List[cell]]]:
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
    screen.onclick(setWire)
    # screen.onclick(trigger, btn=3)

    # Generate cells (64 x 64 cells) - user draws wiring diagram
    w, h = int(screen.width / 10), int(screen.height / 10)
    states = [[0 for _ in range(w)] for _ in range(h)]
    cells = [[cell((x - w//2) * 10, (y - h//2) * 10, states[x][y]) for x in range(w)] for y in range(h)]
    return screen, states, cells

# Main loop
running = True
screen, states, cells = setupWW()
w, h = int(screen.width / 10), int(screen.height / 10)

# Loop
while running:
    # Update cells
    states = wireWorld(states, w, h)

    # Update actual cells
    for y, row in enumerate(states):
        for x, char in enumerate(row):
            cells[y][x].update(char == 1)
    screen.update()
screen.bye()