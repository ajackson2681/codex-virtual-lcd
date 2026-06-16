package dev.adamjackson;

import java.util.Arrays;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class GapBuffer {

    private char[] buffer;
    private int gapStart;
    private int gapEnd;
    private BooleanProperty stale = new SimpleBooleanProperty(false);
    private int viewFrameStart;

    
    public static final int COL_COUNT = 40;
    public static final int ROW_COUNT = 4;
    
    private char[][] frameBuf = new char[ROW_COUNT][COL_COUNT];
    private int cursorRow = 0;
    private int cursorCol = 0;

    public static final int FRAME_SIZE = COL_COUNT * ROW_COUNT;

    public GapBuffer(int initialSize) {
        buffer = new char[initialSize];
        Arrays.fill(buffer, '\0'); 
        gapStart = 0;
        gapEnd = initialSize;
        viewFrameStart = 0;
        cursorRow = 0;
        cursorCol = 0;
    }

    public void insert(char c) {
        buffer[gapStart++] = c;
        
        if (gapStart == gapEnd) {
            resize();
        }

        moveCursorRight();

        refreshFrameBuffer();
    }

    public void insert(String str)
    {
        for (int i = 0; i < str.length(); i++) {
            insert(str.charAt(i));
        }
    }

    private int findPrevLineStart(int from) {
        if (from == 0) {
            return 0;
        }
        
        int pos = from - 1;
        
        // step past the newline that ended this line if present
        if (getCharAt(pos) == '\n' && pos > 0 && getCharAt(from) != '\n') {
            pos--;
        }

        // scan back up to 40 chars to find the line start
        int count = 0;
        while (pos > 0 && count < COL_COUNT) {
            if (getCharAt(pos - 1) == '\n') {
                break;
            }

            pos--;
            count++;

            if (count == COL_COUNT) {
                pos++;
            }
        }
        
        return pos;
        
    }
    
    private int findNextLineStart(int from) {
        
        int col = 0;
        int pos = from;

        while (pos < totalChars()) {
            char c = getCharAt(pos++);

            if (c == '\n') {
                return pos;
            }
            
            if (++col >= COL_COUNT) {
                return pos;
            }
        }
        
        return pos;
    }

    private void moveCursorLeft() {
        cursorCol--;
        if (cursorCol < 0) {
            cursorCol = COL_COUNT - 1;
            cursorRow--;
            if (cursorRow < 0) {
                cursorRow = 0;
                viewFrameStart = findPrevLineStart(viewFrameStart);
                refreshFrameBuffer();
            }
            else {
                // if we moved up a line but are still within the frame, we need to
                // move the column to the end of new current line
                if (frameBuf[cursorRow][cursorCol] == '\0') {
                    // scan left until we find a non-empty character or the start of the line
                    while (cursorCol > 0 && frameBuf[cursorRow][cursorCol] == '\0') {
                        cursorCol--;
                    }
                }
            }
        }
    }

    public void moveLeft(boolean moveCursor) {

        if (gapStart == 0) {
            return;
        }

        buffer[gapEnd - 1] = buffer[gapStart - 1];

        gapStart--;
        gapEnd--;
        if (moveCursor) {
            moveCursorLeft();
        }
    }

    public void moveLeft() {
        moveLeft(true);
    }

    private void moveCursorRight() {
        cursorCol++;
        if (cursorCol == COL_COUNT || buffer[gapStart - 1] == '\n') {
            cursorCol = 0;
            cursorRow++;
            if (cursorRow >= ROW_COUNT) {
                cursorRow = ROW_COUNT - 1;
                viewFrameStart = findNextLineStart(viewFrameStart);
                refreshFrameBuffer();
            }
        }
    }

    
    public void moveRight(boolean moveCursor) {

        if (gapEnd == buffer.length) {
            return;
        }

        buffer[gapStart] = buffer[gapEnd];

        gapStart++;
        gapEnd++;
        if (moveCursor) {
            moveCursorRight();
        }
    }

    public void moveRight() {
        moveRight(true);
    }
    
    public void refreshFrameBuffer2() {
        invalidate();
    }
    
    public void refreshFrameBuffer() {
        for (int i = 0; i < ROW_COUNT; i++) {
            Arrays.fill(frameBuf[i], '\0');
        }
    
        int row = 0, col = 0;
        int totalCh = totalChars();
        
        for (int i = viewFrameStart; i < totalCh && row < ROW_COUNT; i++) {
            char c = getCharAt(i);
            
            frameBuf[row][col] = c;

            if (c == '\n') {
                row++;
                col = 0;
            } 
            else {
                col++;
                if (col >= COL_COUNT) {
                    col = 0;
                    row++;
                }
            }
        }

        invalidate();
    }

    public void resize() {
        int suffixLen = buffer.length - gapEnd;
        int oldSize = buffer.length;

        int newSize = buffer.length * 2;
        this.buffer = Arrays.copyOf(this.buffer, newSize);

        for (int i = gapEnd; i < oldSize; i++) {
            buffer[buffer.length - suffixLen + i - gapEnd] = buffer[i];
        }

        gapEnd = buffer.length - suffixLen;
    }

    public void moveWordLeft() 
    {
        if (gapStart > 0 && (buffer[gapStart - 1] == ' ' || buffer[gapStart - 1] == '\n')) {
            moveLeft(true);
        }

        while (gapStart > 0 && buffer[gapStart - 1] != ' ' && buffer[gapStart - 1] != '\n') {
            moveLeft(true);
        }
    }

    public void moveWordRight() 
    {
        if (gapEnd < buffer.length && (buffer[gapEnd] == ' ' || buffer[gapEnd] == '\n')) {
            moveRight(true);
        }

        while (gapEnd < buffer.length && buffer[gapEnd] != ' ' && buffer[gapEnd] != '\n') {
            moveRight(true);
        }
    }

    public void moveToStart() 
    {
        while (gapStart > 0) {
            this.moveLeft();
        }
        
        viewFrameStart = 0;
        cursorRow = 0;
        cursorCol = 0;

        refreshFrameBuffer();
    }

    public void moveToLineStart()
    {
        while (cursorCol > 0) {
            moveLeft();
        }

        refreshFrameBuffer();
    }

    public void moveToEnd() 
    {
        while (gapEnd < buffer.length) {
            this.moveRight();
        }

        refreshFrameBuffer();
    }

    public void moveToLineEnd()
    {
        while ((frameBuf[cursorRow][cursorCol] != '\n' &&
                frameBuf[cursorRow][cursorCol] != '\0'))  
        {
            if (cursorCol == COL_COUNT - 1) {
                break;
            }
            moveRight();
        }

        refreshFrameBuffer();
    }

    public void invalidate() { 
        stale.set(true); 
    }

    private int frameCellToBufferIndex(int targetRow, int targetCol) {

        int row = 0, col = 0;
        int pos = viewFrameStart;

        while (pos < totalChars()) {
            if (row == targetRow && col == targetCol) {
                return pos;
            } 
            
            char c = getCharAt(pos++);
            if (c == '\n') {
                row++;
                col = 0;
            } 
            else {
                col++;
                if (col >= COL_COUNT) {
                    col = 0;
                    row++;
                }
            }
        }

        return pos;
    }
    
    private void clamp() {
        // clamp to end of target line
        boolean shifted = false;
        while (frameBuf[cursorRow][cursorCol] == '\0' && cursorCol > 0) 
        {
            cursorCol--;
            shifted = true;
        }
        
        // if we actually clamped, and went back past a newline, we should shift
        // back forward one 
        if (shifted && frameBuf[cursorRow][cursorCol] != '\n' && frameBuf[cursorRow][cursorCol] != '\0') {
            cursorCol++;
        }

    }

    public void moveDownOneLine() 
    {
        // check if the next row exists in the frame buffer
        // a row exists if the current row ended with \n, or if it has content
        boolean currentRowHasNewline = false;
        for (int c = 0; c < COL_COUNT; c++) {
            if (frameBuf[cursorRow][c] == '\n') {
                currentRowHasNewline = true;
                break;
            }
        }
        
        if (!currentRowHasNewline && frameBuf[cursorRow][COL_COUNT - 1] == '\0') {
            // move to end of the current line if we're already at the bottom
            while (cursorCol < COL_COUNT && frameBuf[cursorRow][cursorCol] != '\0') {
                cursorCol++;
            }
        }
        else if (cursorRow >= 3) {
            // scroll frame down
            int nextStart = findNextLineStart(viewFrameStart);
            
            if (nextStart >= totalChars()) {
                return; // can't scroll down if there are no more lines
            }

            viewFrameStart = nextStart;
            refreshFrameBuffer();
        } 
        else {
            cursorRow++;
        }
        
        // clamp to end of target line
        clamp();
        
        // sync gap buffer to new cursor position
        int newPos = frameCellToBufferIndex(cursorRow, cursorCol);

        while (gapStart < newPos) {
            moveRight(false);
        }
    }

    public void moveUpOneLine() 
    {   
        if (cursorRow == 0) {
            // scroll frame up
            if (viewFrameStart == 0) {
                return; // can't scroll up if we're already at the top
            }
            viewFrameStart = findPrevLineStart(viewFrameStart);
            refreshFrameBuffer();
            // cursorRow stays the same, now pointing at new content
        } 
        else {
            cursorRow--;
        }
    
        clamp();
        
        // sync gap buffer to new cursor position
        int newPos = frameCellToBufferIndex(cursorRow, cursorCol);

        while(gapStart > newPos) {
            moveLeft(false);
        }
    }

    public void moveToFrameStart() {
        while (gapStart > viewFrameStart) {
            moveLeft();
        }
        refreshFrameBuffer();
    }

    public void moveToFrameEnd() {
        // move to bottom row
        while (cursorRow < ROW_COUNT - 1) 
        {
            if (frameBuf[cursorRow][cursorCol] == '\0' ||
                frameBuf[cursorRow][cursorCol] == '\n') 
            {
                break;
            }
            moveRight();
        }

        moveToLineEnd(); // move to the line end once we've reached the bottom
        // row
        
        refreshFrameBuffer();
    }

    public char getCharAt(int idx) {
        if (idx < 0) {
            return '\0';
        }
        else if (idx >= totalChars()) {
            return '\0';
        }
        
        if (idx < gapStart) {
            return buffer[idx];
        } else {
            return buffer[gapEnd + idx - gapStart];
        }
    }

    public void backSpace()
    {
        if (gapStart == 0) {
            return;
        }

        buffer[gapStart-1] = '\0';
        gapStart--;

        moveCursorLeft();

        refreshFrameBuffer();
    }

    public void backSpaceWord() {
        backSpace();

        while (gapStart > 0 && buffer[gapStart - 1] != ' ' && buffer[gapStart - 1] != '\n') {
            backSpace();
        }
    }
    
    public void deleteChar()
    {
        if (gapEnd == buffer.length) {
            return;
        }
        
        buffer[gapEnd] = '\0';
        gapEnd++;

        refreshFrameBuffer();
    }

    public void deleteWord() {
        deleteChar();

        while (gapEnd < buffer.length && buffer[gapEnd] != ' ') {
            deleteChar();
        }
    }

    public BooleanProperty isStale() { 
        return stale; 
    }

    public char[][] getVisibleFrame() {
        return frameBuf;
    }
    public int getFrameRow() {
        return cursorRow;
    }

    public int getFrameCol() {
        return cursorCol;
    }

    public int totalChars() {
        return gapStart + (buffer.length - gapEnd);
    }
}
