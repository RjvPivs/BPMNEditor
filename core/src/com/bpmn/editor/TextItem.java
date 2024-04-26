package com.bpmn.editor;


import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.Objects;

public class TextItem extends TextArea {
    Direction dirX;
    float firstX;

    public TextItem(String text, Skin skin, Boolean isAction) {
        super(text, skin);
        this.moveCursorLine(0);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (y < TextItem.this.getWidth() - 30 && !isAction){
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                //return super.touchDown(event, x, y, pointer, button);
                if (firstX == -9999 && (abs(x - TextItem.this.getWidth()) < TextItem.this.getWidth() * 0.055)) {
                    firstX = x;
                    dirX = Direction.right;
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                firstX = -9999;
            }
        });
        addListener(new DragListener() {

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                if (!Objects.equals(TextItem.this.getText(), "Input here")){
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    if (firstX == -9999) {
                        TextItem.this.moveBy(x - TextItem.this.getWidth() / 2, y - TextItem.this.getHeight() / 2);
                    } else {
                        if (firstX < 0) {
                            if (dirX == Direction.left) {
                                change(abs(x / firstX));
                            } else {
                                change(abs(firstX / x));
                            }
                            change(abs(firstX / x));
                        } else {
                            if (dirX == Direction.left) {
                                change(abs(firstX / x));
                            } else {
                                change(abs(x / firstX));
                            }
                        }
                        firstX = x;
                    }
                    Gdx.input.setOnscreenKeyboardVisible(false);
                }
            }
        });
    }

    public void change(float f) {
        System.out.println(f);
        setBounds(getX(), getY(), getWidth() * f, getHeight() * f);
    }

    @Override
    protected void positionChanged() {
        setPosition(getX(), getY());
        super.positionChanged();
    }

    enum Direction {
        right,
        left,
        up,
        down
    }
}

