package com.bpmn.editor.editor;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cubemap;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g3d.decals.CameraGroupStrategy;
import com.badlogic.gdx.graphics.g3d.decals.Decal;
import com.badlogic.gdx.graphics.g3d.decals.DecalBatch;
import com.badlogic.gdx.graphics.g3d.utils.FirstPersonCameraController;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ScreenUtils;
import com.bpmn.editor.data.DatabaseModule;
import com.bpmn.editor.data.MongoRepository;
import com.bpmn.editor.model.Actor;
import com.bpmn.editor.model.Scheme;
import com.bpmn.editor.view.Launcher3D;
import com.kotcrab.vis.ui.VisUI;
import com.kotcrab.vis.ui.widget.CollapsibleWidget;
import com.kotcrab.vis.ui.widget.VisTable;

import net.mgsx.gltf.loaders.gltf.GLTFLoader;
import net.mgsx.gltf.scene3d.attributes.PBRCubemapAttribute;
import net.mgsx.gltf.scene3d.attributes.PBRTextureAttribute;
import net.mgsx.gltf.scene3d.lights.DirectionalLightEx;
import net.mgsx.gltf.scene3d.scene.Scene;
import net.mgsx.gltf.scene3d.scene.SceneAsset;
import net.mgsx.gltf.scene3d.scene.SceneManager;
import net.mgsx.gltf.scene3d.utils.IBLBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import games.rednblack.gdxar.GdxArApplicationListener;
import games.rednblack.gdxar.GdxFrame;
import kotlin.Pair;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;

public class ThreeDViewer extends GdxArApplicationListener implements InputProcessor {
    private final MongoRepository repository = DatabaseModule.INSTANCE.provideMongoRepository(DatabaseModule.INSTANCE.provideRealm());
    Scheme scheme;
    Launcher3D launcher3D;
    DecalBatch decalBatch;

    public ThreeDViewer(String s, Launcher3D launcher3D) {
        try {
            scheme = BuildersKt.runBlocking(EmptyCoroutineContext.INSTANCE,
                    (scope, continuation) -> repository.getScheme(s, continuation));
        } catch (InterruptedException e) {

        }
        this.launcher3D = launcher3D;
    }


    private Matrix4 playerTransform = new Matrix4();
    private final Vector3 moveTranslation = new Vector3();
    private final Vector3 currentPosition = new Vector3();
    private SceneManager sceneManager;
    private SceneAsset sceneAsset;
    private Scene scene;
    private Scene scene2;
    private PerspectiveCamera camera;
    private Cubemap diffuseCubemap;
    private Cubemap environmentCubemap;
    private Cubemap specularCubemap;
    private Texture brdfLUT;
    private float time;
    private DirectionalLightEx light;
    State state;
    Stage stage;
    private FirstPersonCameraController cameraController;
    ArrayList<Pair<Vector3, Pair<String, Boolean>>> texts;

    @Override
    public void renderARModels(GdxFrame frame) {

    }

    @Override
    public void create() {
        stage = new Stage();
        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);
        state = State.THREED;
        if (!VisUI.isLoaded()) {
            VisUI.load();
        }
        texts = new ArrayList<>();
        getArAPI().setPowerSaveMode(false);
        getArAPI().setAutofocus(true);
        getArAPI().enableSurfaceGeometry(true);
        getArAPI().setRenderAR(false);
        VisTable modes = new VisTable(true);
        TableButton twoD = new TableButton("modes/2DOFF.png");
        TableButton threeD = new TableButton("modes/3DON.png");
        TableButton AR = new TableButton("modes/AR.png");
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
            }
        });
        threeD.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                state = State.THREED;
                getArAPI().setRenderAR(false);
                System.out.println("TOUCHED");
            }
        });
        modes.add(twoD);
        modes.add(threeD);
        modes.add(AR);
        VisTable modess = new VisTable(true);
        CollapsibleWidget modesWidget = new CollapsibleWidget(modes);
        modess.add(modesWidget);
        stage.addActor(modess);
        modess.setPosition(stage.getWidth() - 150, stage.getHeight() - 50);

        Stage stage = new Stage();
        sceneManager = new SceneManager();
        camera = new PerspectiveCamera(60f, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        float d = .02f;
        camera.near = d / 10f;
        camera.far = 50000;
        sceneManager.setCamera(camera);
        camera.position.set(0, 0.5f, 17f);
        decalBatch = new DecalBatch(new CameraGroupStrategy(camera));
        // create scene
        for (Actor actor : scheme.getActors()) {
            Actor actor1 = new Actor();
            actor1.setCoordX(actor.getCoordX());
            actor1.setCoordY(actor.getCoordY());
            switch (actor.getSprite()) {
                case "simple/simpleBasic1.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/simple1.gltf"));
                    break;
                case "simple/simpleBasic2.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/simple2.gltf"));
                    break;
                case "simple/simpleBasic3.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/simple3.gltf"));
                    break;
                case "messages/message1Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/message1.gltf"));
                    break;
                case "messages/message2Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/message2.gltf"));
                    break;
                case "messages/message3Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/message3.gltf"));
                    break;
                case "messages/message4Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/message4.gltf"));
                    break;
                case "timer/timer1Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/timer1.gltf"));
                    break;
                case "timer/timer2Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/timer2.gltf"));
                    break;
                case "connections/arrowB.png":
                    actor1.setCoordY(actor.getCoordY() - 50);
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrow.gltf"));
                    break;
                case "connections/arrowDotB.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrowDot.gltf"));
                    break;
                case "connections/arrowDownB.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrowDown.gltf"));
                    break;
                case "connections/arrowUpB.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrowUp.gltf"));
                    break;
                case "connections/arrowDownDotB.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrowDownDot.gltf"));
                    break;
                case "connections/arrowUpDotB.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/arrowUpDot.gltf"));
                    break;
                case "style/321.json":
                    float scale = 0;
                    actor1.setCoordX(actor.getRoleY() - 1500 + 1600 + 500 - (100 + 5 * scale) * scale);
                    actor1.setCoordY(actor.getRoleX() - 2050 + 1415);
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/role.gltf"));
                    break;
                case "logic/logic1Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/logic1.gltf"));
                    break;
                case "logic/logic2Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/logic2.gltf"));
                    break;
                case "logic/logic3Basic.png":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/logic3.gltf"));
                    break;
                case "style/123.json":
                    sceneAsset = new GLTFLoader().load(Gdx.files.internal("3dmodels/task.gltf"));
                    break;
            }
            scene = new Scene(sceneAsset.scene);

            if (actor.getSprite().equals("style/123.json")) {
                scene.modelInstance.transform.translate((((actor1.getCoordX()) - stage.getWidth() - 2) / 50 + 20f), (((actor1.getCoordY()) - stage.getHeight() / 2) / 50), -0.5f);
                texts.add(new Pair<>(new Vector3(((actor1.getCoordX()) - stage.getWidth() - 2) / 50 + 22f, ((actor1.getCoordY()) - stage.getHeight() / 2) / 50+ 1f, 0f), new Pair<>(actor.getText(), false)));
            } else if (!actor.getSprite().equals("style/321.json")) {
                scene.modelInstance.transform.translate((((actor1.getCoordX()) - stage.getWidth() - 2) / 50 + 20f), (((actor1.getCoordY()) - stage.getHeight() / 2) / 50), -0.5f);
            } else {
                //float scaling = actor.getScale() == 1f ? 0f : actor.getScale() * 1.7f;
                System.out.println("BEBRA");
                System.out.println(actor.getScale());
                float scale;
                if (actor.getScale() == 1.0) {
                    scale = 0;
                } else if (actor.getScale() > 1 && actor.getScale() < 2) {
                    scale = actor.getScale() * actor.getScale() * 0.5f;
                } else {
                    scale = (float) (actor.getScale() * Math.sqrt(actor.getScale()));
                }
                System.out.println("ABOBA");
                System.out.println(actor.getScale());
                float scale1 = actor.getScale() < 2 ? -scale * (2f + actor.getScale()) : -scale * (0.3f + actor.getScale());
                float scale2 = scale1 != 0 ? scale1 - 2f : 0;
                scene.modelInstance.transform.translate(((actor1.getCoordX()) - stage.getWidth() - 2) / 50 + 20f, ((actor1.getCoordY()) - stage.getHeight() / 2) / 50, 0f);
                scene.modelInstance.transform.translate(scale1, 0f, 0f);
                texts.add(new Pair<>(new Vector3(((actor1.getCoordX()) - stage.getWidth() - 2) / 50 + 17f + scale2, ((actor1.getCoordY()) - stage.getHeight() / 2) / 50, 0f), new Pair<>(actor.getText(), true)));
            }
            scene.modelInstance.transform.scale(actor.getScale(), actor.getScale(), 1f);
            scene.modelInstance.transform.translate(1f, 1f, 1f);
            sceneManager.addScene(scene);
        }
        cameraController = new FirstPersonCameraController(camera);
        light = new DirectionalLightEx();
        light.direction.set(1, -3, 1).nor();
        light.color.set(Color.WHITE);
        sceneManager.environment.add(light);

        IBLBuilder iblBuilder = IBLBuilder.createOutdoor(light);
        environmentCubemap = iblBuilder.buildEnvMap(1024);
        diffuseCubemap = iblBuilder.buildIrradianceMap(256);
        specularCubemap = iblBuilder.buildRadianceMap(10);
        iblBuilder.dispose();
        brdfLUT = new Texture(Gdx.files.classpath("net/mgsx/gltf/shaders/brdfLUT.png"));
        sceneManager.setAmbientLight(1f);
        sceneManager.environment.set(new PBRTextureAttribute(PBRTextureAttribute.BRDFLUTTexture, brdfLUT));
        sceneManager.environment.set(PBRCubemapAttribute.createSpecularEnv(specularCubemap));
        sceneManager.environment.set(PBRCubemapAttribute.createDiffuseEnv(diffuseCubemap));
    }

    @Override
    public void resize(int width, int height) {
        sceneManager.updateViewport(width, height);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void render() {
        if (state == State.THREED) {
            ScreenUtils.clear(255, 255, 255, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }
        float deltaTime = Gdx.graphics.getDeltaTime();
        time += deltaTime;
        processInput(deltaTime);
        sceneManager.update(deltaTime);
        sceneManager.render();
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
        for (Pair<Vector3, Pair<String, Boolean>> v : texts) {
            byte[] textByte = TextGenerator.generate(v.getSecond().getFirst());
            File file = new File(Gdx.files.getLocalStoragePath(), "temp.png");
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                FileOutputStream fs = new FileOutputStream(file);
                fs.write(textByte);
                fs.close();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            Texture t = new Texture(Gdx.files.local("temp.png"));
            TextureRegion tt = new TextureRegion(t);
            Decal dd = Decal.newDecal(tt);
            dd.setBlending(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            dd.setPosition(v.getFirst());
            dd.setHeight(0.7f);
            dd.setWidth(1.5f);
            if (v.getSecond().getSecond()){
                dd.rotateZ(90);
            }
            decalBatch.add(dd);
            Gdx.files.local("temp.png").delete();
        }

        decalBatch.flush();
    }

    private void processInput(float deltaTime) {
        playerTransform.set(scene.modelInstance.transform);
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            moveTranslation.z += 8 * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            moveTranslation.z -= 8 * deltaTime;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.Q)) {
            playerTransform.rotate(Vector3.X, 80 * deltaTime);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.E)) {
            playerTransform.rotate(Vector3.X, -80 * deltaTime);
        }
        playerTransform.translate(moveTranslation);
        scene.modelInstance.transform.set(playerTransform);
        scene.modelInstance.transform.getTranslation(currentPosition);
        moveTranslation.set(0, 0, 0);
    }

    @Override
    public void dispose() {
        sceneManager.dispose();
        sceneAsset.dispose();
        environmentCubemap.dispose();
        diffuseCubemap.dispose();
        specularCubemap.dispose();
        brdfLUT.dispose();
        stage.dispose();
    }

    private int dragX, dragY;

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        dragX = screenX;
        dragY = screenY;
        return true;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        float dX = (float) (screenX - dragX);
        float dY = (float) (dragY - screenY);
        dragX = screenX;
        dragY = screenY;
        //camera.position.rotate(Vector3.X, dX/ 50f);
        //camera.position.rotate(Vector3.Y, dY/ 50f);
        //camera.rotateAround();
        camera.rotateAround(new Vector3(0, 0, 0), Vector3.Y, (float) Math.atan2(dX * Math.cos((float) Math.atan2(dY, camera.position.z)), camera.position.z) * 1.35f);
        camera.update();
        camera.rotateAround(new Vector3(0, 0, 0), Vector3.X, (float) Math.atan2(dY, camera.position.z) * 1.3f);
        camera.update();
        return true;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

}
