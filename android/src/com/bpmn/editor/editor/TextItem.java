package com.bpmn.editor.editor;


import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.bpmn.editor.data.DatabaseModule;
import com.bpmn.editor.data.MongoRepository;
import com.bpmn.editor.model.Scheme;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class TextItem extends TextArea {
    private final MongoRepository repository = DatabaseModule.INSTANCE.provideMongoRepository(DatabaseModule.INSTANCE.provideRealm());
    com.bpmn.editor.model.Actor actorContainer;
    Direction dirX;
    float startWidth;
    float firstX;
    long last;
    String sprite;
    String txt;
    Scheme scheme;
    float roleX;
    float roleY;

    public TextItem(String text, Skin skin, Boolean isAction, Scheme scheme, com.bpmn.editor.model.Actor actor, float roleX, float roleY) {
        super(text, skin);
        this.scheme = scheme;
        last = -9999;
        if (isAction) {
            sprite = "style/123.json";
        } else {
            sprite = "style/321.json";
        }
        startWidth = getWidth();
        txt = text;
        this.roleX = roleX;
        this.roleY = roleY;
        if (actor == null) {
            actor = new com.bpmn.editor.model.Actor();
            actor.setCoordX(getX());
            actor.setCoordY(getY());
            actor.setHeight(getHeight());
            actor.setWidth(getWidth());
            actor.setType("TextItem");
            actor.setSprite(sprite);
            actor.setText(txt);
            actor.setRoleX(this.roleX);
            actor.setRoleY(this.roleY);

            try {
                com.bpmn.editor.model.Actor finalActor1 = actor;
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.insertActor(this.scheme, finalActor1, continuation));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setBounds(actor.getCoordX(), actor.getCoordY(), actor.getWidth() + 30, actor.getHeight());
            startWidth = getWidth();
            actor.setScale(getWidth() / startWidth);
        } else {
            setBounds(actor.getCoordX(), actor.getCoordY(), actor.getWidth(), actor.getHeight());
            startWidth = getWidth();
        }
        actorContainer = actor;
        this.moveCursorLine(0);
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (y < TextItem.this.getWidth() - 30 && !isAction) {
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
                if (!Objects.equals(TextItem.this.getText(), "Input here")) {
                    Gdx.input.setOnscreenKeyboardVisible(false);
                    if (firstX == -9999) {
                        TextItem.this.moveBy(x - TextItem.this.getWidth() / 2, y - TextItem.this.getHeight() / 2);
                        TextItem.this.roleX += x - TextItem.this.getWidth() / 2;
                        TextItem.this.roleY -= y - TextItem.this.getHeight() / 2;
                        System.out.println("CHECKER");
                        System.out.println(TextItem.this.getX());
                        System.out.println(TextItem.this.getY());
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
        setBounds(getX(), getY(), getWidth() * f, getHeight() * f);
        com.bpmn.editor.model.Actor finalActor = actorContainer;
        com.bpmn.editor.model.Actor ff = new com.bpmn.editor.model.Actor();
        ff.set_id(finalActor.get_id());
        ff.setCoordX(finalActor.getCoordX());
        ff.setCoordY(finalActor.getCoordY());
        ff.setSprite(finalActor.getSprite());
        ff.setWidth(getWidth());
        ff.setHeight(getHeight());
        ff.setType(finalActor.getType());
        ff.setText(getText());
        ff.setRoleX(roleX);
        ff.setRoleY(roleY);
        ff.setScale(TextItem.this.getWidth() / TextItem.this.startWidth);
        ff.setStartWidth(startWidth);
        try {
            BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                    (scope, continuation) -> repository.updateActor(this.scheme, ff, continuation));
            actorContainer = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                    (scope, continuation) -> repository.getActor(ff, continuation));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void positionChanged() {
        setPosition(getX(), getY());
        super.positionChanged();
        if (actorContainer != null) {
            com.bpmn.editor.model.Actor finalActor = actorContainer;
            com.bpmn.editor.model.Actor f = new com.bpmn.editor.model.Actor();
            f.set_id(finalActor.get_id());
            f.setCoordX(getX());
            f.setCoordY(getY());
            f.setSprite(finalActor.getSprite());
            f.setWidth(getWidth());
            f.setHeight(getHeight());
            f.setType(finalActor.getType());
            f.setText(getText());
            f.setRoleX(roleX);
            f.setRoleY(roleY);
            f.setScale(finalActor.getScale());
            f.setStartWidth(startWidth);
            try {
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.updateActor(this.scheme, f, continuation));
                actorContainer = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.getActor(f, continuation));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

    enum Direction {
        right,
        left,
        up,
        down
    }
}

