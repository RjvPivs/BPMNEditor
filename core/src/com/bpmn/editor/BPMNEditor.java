package com.bpmn.editor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

import java.awt.Rectangle;

import games.rednblack.gdxar.GdxArApplicationListener;
import games.rednblack.gdxar.GdxFrame;

public class BPMNEditor extends GdxArApplicationListener {
    Stage stage;
    Group group;
    Rectangle rect;
    SpriteBatch batch;
    OrthographicCamera camera;
    float MenuWidth;
    State state;

    @Override
    public void renderARModels(GdxFrame frame) {

    }

    @Override
    public void create() {
        getArAPI().setPowerSaveMode(false);
        getArAPI().setAutofocus(true);
        getArAPI().enableSurfaceGeometry(true);
        state = State.TWOD;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        VisUI.load();
        MenuWidth = stage.getWidth() / 4;
        TableButton twoD = new TableButton("modes/2D.png");
        TableButton threeD = new TableButton("modes/3D.png");
        TableButton AR = new TableButton("modes/AR.png");
        VisTable modes = new VisTable(true);
        AR.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.AR;
                getArAPI().setRenderAR(true);
            }
        });
        twoD.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.TWOD;
                getArAPI().setRenderAR(false);
            }
        });
        modes.add(twoD);
        modes.add(threeD);
        modes.add(AR);
        CollapsibleWidget modesWidget = new CollapsibleWidget(modes);
        VisTable modess = new VisTable(true);
        modess.add(modesWidget);
        stage.addActor(modess);
        modess.setPosition(stage.getWidth() - 150, stage.getHeight() - 50);
        TableButton logo = new TableButton("Union.png");
        VisTable logoTable = new VisTable();
        logoTable.add(logo).row();
        CollapsibleWidget logoCollapsible = new CollapsibleWidget(logoTable);
        Texture texture = new Texture(Gdx.files.internal("frame.png"));
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        VisImage image = new VisImage(texture);
        image.setBounds(0, 0, 500, stage.getHeight());

        stage.addActor(image);

        VisTable table = new VisTable(true);
        //table.debugAll();

        TableButton buttonEvent = new TableButton("buttons/buttonEvent.png");
        VisTable eventTable = new VisTable();
        eventTable.add(buttonEvent);
        CollapsibleWidget eventCollapsible = new CollapsibleWidget(eventTable);
        TableButton simple1 = new TableButton("simple/simple1.png");
        TableButton simple2 = new TableButton("simple/simple2.png");
        TableButton simple3 = new TableButton("simple/simple3.png");
        simple1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("simple/simpleBasic1.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        simple2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("simple/simpleBasic2.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        simple3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("simple/simpleBasic3.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        VisTable simpleTable = new VisTable(true);
        //simpleTable.debugAll();
        simpleTable.setWidth(table.getWidth());
        simpleTable.align(Align.center);
        simpleTable.add(simple1);
        simpleTable.add(simple2).align(Align.center);
        simpleTable.add(simple3).expand().row();
        final CollapsibleWidget collapsibleSimple = new CollapsibleWidget(simpleTable);
        collapsibleSimple.setWidth(table.getWidth());
        collapsibleSimple.setCollapsed(true);


        TableButton buttonAction = new TableButton("buttons/buttonActions.png");
        VisTable actionTable = new VisTable();
        actionTable.add(buttonAction);
        CollapsibleWidget actionCollapsible = new CollapsibleWidget(actionTable);
        TableButton message1 = new TableButton("messages/message1.png");
        TableButton message2 = new TableButton("messages/message2.png");
        TableButton message3 = new TableButton("messages/message3.png");
        TableButton message4 = new TableButton("messages/message4.png");
        message1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("messages/message1Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        message2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("messages/message2Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        message3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("messages/message3Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        message4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("messages/message4Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        simpleTable.add(message1);
        simpleTable.add(message2);
        simpleTable.add(message3);
        simpleTable.add(message4);
        simpleTable.row();
        VisTable messageTable = new VisTable(true);

        TableButton timer1 = new TableButton("timer/timer1.png");
        TableButton timer2 = new TableButton("timer/timer2.png");
        timer1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("timer/timer1Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        timer2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("timer/timer2Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        simpleTable.add(timer1);
        simpleTable.add(timer2).align(Align.center).row();

        TableButton action = new TableButton("tasks/task.png");
        VisTable actTable = new VisTable(true);
        actTable.add(action);
        final CollapsibleWidget collapsibleAct = new CollapsibleWidget(actTable);
        collapsibleAct.setCollapsed(true);

        action.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Skin skin = new Skin(Gdx.files.internal("style/123.json"));
                TextItem actor = new TextItem("Input here", skin, true);
                actor.setBounds(stage.getWidth() / 2, stage.getHeight() / 2, 180, 124);
                //actor.setZIndex(1);
                actor.setZIndex(actor.getZIndex() + 1);
                stage.addActor(actor);
            }
        });

        TableButton buttonLogic = new TableButton("buttons/buttonLogic.png");
        VisTable logicTable = new VisTable();
        logicTable.add(buttonLogic);
        CollapsibleWidget logicCollapsible = new CollapsibleWidget(logicTable);
        TableButton logic1 = new TableButton("logic/logic1.png");
        TableButton logic2 = new TableButton("logic/logic2.png");
        TableButton logic3 = new TableButton("logic/logic3.png");
        VisTable logTable = new VisTable(true);
        logTable.add(logic1);
        logTable.add(logic2);
        logTable.add(logic3).align(Align.center).row();
        final CollapsibleWidget collapsibleLogic = new CollapsibleWidget(logTable);
        collapsibleLogic.setCollapsed(true);

        logic1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("logic/logic1Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        logic2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("logic/logic2Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        logic3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("logic/logic3Basic.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
                //Gdx.input.setInputProcessor(new GestureDetector(new DoubleTapDetector(actor)));
            }
        });
        TableButton buttonConnection = new TableButton("buttons/buttonConnection.png");
        VisTable connectionTable = new VisTable();
        connectionTable.add(buttonConnection);
        CollapsibleWidget connectionCollapsible = new CollapsibleWidget(connectionTable);
        TableButton connection = new TableButton("connections/arrow.png");
        TableButton connection0 = new TableButton("connections/arrowDot.png");
        TableButton connection1 = new TableButton("connections/arrowUp.png");
        TableButton connection2 = new TableButton("connections/arrowUpDot.png");
        TableButton connection3 = new TableButton("connections/arrowDown.png");
        TableButton connection4 = new TableButton("connections/arrowDownDot.png");
        VisTable connectionsTable = new VisTable(true);
        connectionsTable.add(connection);
        connectionsTable.add(connection0).row();
        connectionsTable.add(connection1);
        connectionsTable.add(connection2).row();
        connectionsTable.add(connection3);
        connectionsTable.add(connection4).align(Align.center).row();
        final CollapsibleWidget collapsibleConnection = new CollapsibleWidget(connectionsTable);
        collapsibleConnection.setCollapsed(true);
        connection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        connection0.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowDotB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        connection1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowUpB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        connection2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowUpDotB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        connection3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowDownB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        connection4.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                BPMNItem actor = new BPMNItem("connections/arrowDownDotB.png");
                actor.setPosition(stage.getWidth() / 2, stage.getHeight() / 2);
                stage.addActor(actor);
            }
        });
        TableButton buttonRoles = new TableButton("buttons/buttonRoles.png");
        VisTable rolesTable = new VisTable();
        rolesTable.add(buttonRoles);
        CollapsibleWidget rolesCollapsible = new CollapsibleWidget(rolesTable);
        TableButton role = new TableButton("roles/role.png");
        VisTable roleTable = new VisTable(true);
        roleTable.add(role).align(Align.center);
        final CollapsibleWidget collapsibleRole = new CollapsibleWidget(roleTable);
        collapsibleRole.setCollapsed(true);

        role.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Skin skin = new Skin(Gdx.files.internal("style/321.json"));
                TextItem actor = new TextItem("Input here", skin, false);
                actor.setBounds(stage.getWidth() - 1500, -2000, 180, 450);
                Group group = new Group();
                group.addActor(actor);
                group.rotateBy(90);
                actor.moveBy(-325, 550);
                System.out.println("Aboba");
                System.out.println(group.getZIndex());
                stage.addActor(group);
            }
        });
        buttonRoles.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapsibleAct.setCollapsed(true);
                collapsibleLogic.setCollapsed(true);
                collapsibleSimple.setCollapsed(true);
                collapsibleConnection.setCollapsed(true);
                collapsibleRole.setCollapsed(!collapsibleRole.isCollapsed());
            }
        });
        buttonConnection.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapsibleRole.setCollapsed(true);
                collapsibleAct.setCollapsed(true);
                collapsibleLogic.setCollapsed(true);
                collapsibleSimple.setCollapsed(true);
                collapsibleConnection.setCollapsed(!collapsibleConnection.isCollapsed());
            }
        });
        buttonAction.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapsibleRole.setCollapsed(true);
                collapsibleConnection.setCollapsed(true);
                collapsibleSimple.setCollapsed(true);
                collapsibleLogic.setCollapsed(true);
                collapsibleAct.setCollapsed(!collapsibleAct.isCollapsed());
            }
        });
        buttonLogic.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapsibleRole.setCollapsed(true);
                collapsibleConnection.setCollapsed(true);
                collapsibleSimple.setCollapsed(true);
                collapsibleAct.setCollapsed(true);
                collapsibleLogic.setCollapsed(!collapsibleLogic.isCollapsed());
            }
        });
        buttonEvent.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                collapsibleRole.setCollapsed(true);
                collapsibleLogic.setCollapsed(true);
                collapsibleConnection.setCollapsed(true);
                collapsibleAct.setCollapsed(true);
                collapsibleSimple.setCollapsed(!collapsibleSimple.isCollapsed());
            }
        });
        table.setPosition(250, stage.getHeight());
        table.add(logoCollapsible).row();
        table.add(eventCollapsible).fillX().expandX().row();
        table.add(collapsibleSimple).row();
        table.add(actionCollapsible).fillX().expandX().row();
        table.add(collapsibleAct).row();
        table.add(logicCollapsible).fillX().expandX().row();
        table.add(collapsibleLogic).row();
        table.add(connectionCollapsible).row();
        table.add(collapsibleConnection).fillX().expandX().row();
        table.add(rolesCollapsible).row();
        table.add(collapsibleRole).fillX().expandX().row();
        stage.addActor(table);
        table.align(Align.top);
    }

    @Override
    public void render() {
        if (state == State.TWOD) {
            ScreenUtils.clear(255, 255, 255, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    enum State {
        AR,
        TWOD,
        THREED
    }
}
