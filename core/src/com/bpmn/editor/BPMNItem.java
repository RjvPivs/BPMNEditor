package com.bpmn.editor;

import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;

public class BPMNItem extends Actor {
    Sprite sprite;
    float firstX;

    public BPMNItem(String s) {
        firstX = -9999;
        sprite = new Sprite(new Texture(Gdx.files.internal(s)));
        sprite.setX(300);
        sprite.setY(300);
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //return super.touchDown(event, x, y, pointer, button);
                if (firstX == -9999 && (abs(x - BPMNItem.this.getWidth()) < BPMNItem.this.getWidth() * 0.03)) {
                    firstX = x;
                }
                //System.out.println(x);
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
                if (firstX == -9999) {
                    BPMNItem.this.moveBy(x - BPMNItem.this.getWidth() / 2, y - BPMNItem.this.getHeight() / 2);
                } else {
                    if (firstX < 0) {
                        change(abs(firstX / x));
                    } else {
                        change(abs(x / firstX));
                    }
                    firstX = x;
                }
            }
        });
    }
    public void change(float f) {
        sprite.setSize((float) (sprite.getWidth() * f), (float) (sprite.getHeight() * f));
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        sprite.draw(batch);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    @Override
    protected void positionChanged() {
        sprite.setPosition(getX(), getY());
        super.positionChanged();
    }

}
