package com.bpmn.editor.editor;

import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.bpmn.editor.data.DatabaseModule;
import com.bpmn.editor.data.MongoRepository;
import com.bpmn.editor.model.Scheme;

import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

/**
 * Расширенный актор.
 */
public class BPMNItem extends Actor {
    private final MongoRepository repository = DatabaseModule.INSTANCE.provideMongoRepository(DatabaseModule.INSTANCE.provideRealm());
    Sprite sprite;
    float firstX;
    long last;
    Stage border;
    float startWidth;
    Scheme scheme = null;
    com.bpmn.editor.model.Actor actorContainer;

    /**
     * Конструктор.
     * @param s Путь до спрайта.
     * @param scheme Схема, к которой актор относится.
     * @param actor Актор для восстановления на сцене.
     * @param border Границы, за которые нельзя перемещать актор.
     */
    public BPMNItem(String s, Scheme scheme, com.bpmn.editor.model.Actor actor, Stage border) {
        this.scheme = scheme;
        last = -9999;
        this.border = border;
        if (actor == null) {
            sprite = new Sprite(new Texture(Gdx.files.internal(s)));
            actor = new com.bpmn.editor.model.Actor();
            actor.setCoordX(sprite.getX());
            actor.setCoordY(sprite.getY());
            actor.setHeight(sprite.getHeight());
            actor.setWidth(sprite.getWidth());
            actor.setType("BPMNItem");
            actor.setSprite(s);
            actor.setScale(1f);

            try {
                com.bpmn.editor.model.Actor finalActor1 = actor;
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.insertActor(this.scheme, finalActor1, continuation));

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        } else {
            sprite = new Sprite(new Texture(Gdx.files.internal(actor.getSprite())));
            sprite.setBounds(actor.getCoordX(), actor.getCoordY(), actor.getWidth(), actor.getHeight());
            setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        }
        actorContainer = actor;
        firstX = -9999;
        startWidth = getWidth();
        addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                //return super.touchDown(event, x, y, pointer, button);
                if (firstX == -9999 && (abs(x - BPMNItem.this.getWidth()) < BPMNItem.this.getWidth() * 0.05)) {
                    firstX = x;
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
                if (firstX == -9999) {
                    if (BPMNItem.this.getX() + x - BPMNItem.this.getWidth() / 2 < border.getWidth() / 4){
                        BPMNItem.this.setX(border.getWidth() / 4);
                        BPMNItem.this.moveBy(0, y - BPMNItem.this.getHeight() / 2);
                    } else if(BPMNItem.this.getY() + y - BPMNItem.this.getHeight() / 2> border.getHeight()- BPMNItem.this.getHeight()){
                        BPMNItem.this.moveBy(x - BPMNItem.this.getWidth() / 2, 0);
                        BPMNItem.this.setY(border.getHeight() - BPMNItem.this.getHeight());
                    } else if(BPMNItem.this.getY() + y - BPMNItem.this.getHeight() / 2 < 0 ){
                        BPMNItem.this.setY(0);
                        BPMNItem.this.moveBy(x - BPMNItem.this.getWidth() / 2, 0);
                    }
                    else {
                        BPMNItem.this.moveBy(x - BPMNItem.this.getWidth() / 2, y - BPMNItem.this.getHeight() / 2);
                    }
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

    /**
     * Метод изменения размера
     * @param f Во сколько раз изменился размер.
     */
    public void change(float f) {
        sprite.setSize((float) (sprite.getWidth() * f), (float) (sprite.getHeight() * f));
        setBounds(sprite.getX(), sprite.getY(), sprite.getWidth(), sprite.getHeight());
        com.bpmn.editor.model.Actor finalActor = actorContainer;
        com.bpmn.editor.model.Actor ff = new com.bpmn.editor.model.Actor();
        ff.set_id(finalActor.get_id());
        ff.setCoordX(finalActor.getCoordX());
        ff.setCoordY(finalActor.getCoordY());
        ff.setSprite(finalActor.getSprite());
        ff.setWidth(sprite.getWidth());
        ff.setHeight(sprite.getHeight());
        ff.setType(finalActor.getType());
        ff.setScale(BPMNItem.this.getWidth() / BPMNItem.this.startWidth);
        System.out.println("Hello");
        System.out.println(ff.getScale());
        try {
            BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                    (scope, continuation) -> repository.updateActor(BPMNItem.this.scheme, ff, continuation));
            actorContainer = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                    (scope, continuation) -> repository.getActor(ff, continuation));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
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
        if (actorContainer != null) {
            com.bpmn.editor.model.Actor finalActor = actorContainer;
            com.bpmn.editor.model.Actor f = new com.bpmn.editor.model.Actor();
            f.set_id(finalActor.get_id());
            f.setCoordX(sprite.getX());
            f.setCoordY(sprite.getY());
            f.setSprite(finalActor.getSprite());
            f.setWidth(finalActor.getWidth());
            f.setHeight(finalActor.getHeight());
            f.setType(finalActor.getType());
            f.setScale(finalActor.getScale());
            try {
                BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.updateActor(BPMNItem.this.scheme, f, continuation));
                actorContainer = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                        (scope, continuation) -> repository.getActor(f, continuation));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
