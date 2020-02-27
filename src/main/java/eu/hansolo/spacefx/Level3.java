/*
 * Copyright (c) 2020 by Gerrit Grunwald
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.hansolo.spacefx;

import javafx.scene.image.Image;

import static eu.hansolo.spacefx.Config.BKG_SCALING_FACTOR;
import static eu.hansolo.spacefx.Config.SCALING_FACTOR;
import static com.gluonhq.attach.util.Platform.isDesktop;
import static com.gluonhq.attach.util.Platform.isIOS;


public class Level3 implements Level {
    public final Difficulty difficulty              = Difficulty.HARD;
    //public final Image      backgroundImg           = new Image(getClass().getResourceAsStream("backgroundL3.jpg"), 700 * BKG_SCALING_FACTOR, 3379 * BKG_SCALING_FACTOR, true, false);
    public final Image      backgroundImg           = isDesktop() ? new Image(getClass().getResourceAsStream("backgroundL3.jpg"), 700 * BKG_SCALING_FACTOR, 3379 * BKG_SCALING_FACTOR, true, false) : isIOS() ? new Image(getClass().getResourceAsStream("backgroundL3.jpg"), 700 * BKG_SCALING_FACTOR, 3379 * BKG_SCALING_FACTOR, true, false) : new Image(getClass().getResourceAsStream("backgroundL3.png"), 700 * BKG_SCALING_FACTOR, 3379 * BKG_SCALING_FACTOR, true, false);
    public final Image[]    enemyImages             = { new Image(getClass().getResourceAsStream("enemy1L3.png"), 60 * SCALING_FACTOR, 60 * SCALING_FACTOR, true, false),
                                                        new Image(getClass().getResourceAsStream("enemy2L3.png"), 60 * SCALING_FACTOR, 60 * SCALING_FACTOR, true, false),
                                                        new Image(getClass().getResourceAsStream("enemy3L3.png"), 68 * SCALING_FACTOR, 68 * SCALING_FACTOR, true, false),
                                                        new Image(getClass().getResourceAsStream("enemy4L3.png"), 68 * SCALING_FACTOR, 68 * SCALING_FACTOR, true, false),
                                                        new Image(getClass().getResourceAsStream("enemy5L3.png"), 68 * SCALING_FACTOR, 68 * SCALING_FACTOR, true, false)};
    public final Image      enemyBossImg0           = new Image(getClass().getResourceAsStream("enemyBoss0L3.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    public final Image      enemyBossImg1           = new Image(getClass().getResourceAsStream("enemyBoss1L3.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    public final Image      enemyBossImg2           = new Image(getClass().getResourceAsStream("enemyBoss2L3.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    public final Image      enemyBossImg3           = new Image(getClass().getResourceAsStream("enemyBoss3L3.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    public final Image      enemyBossImg4           = new Image(getClass().getResourceAsStream("enemyBoss4L3.png"), 100 * SCALING_FACTOR, 100 * SCALING_FACTOR, true, false);
    public final Image      enemyBombImg            = new Image(getClass().getResourceAsStream("enemyBombL3.png"), 20 * SCALING_FACTOR, 20 * SCALING_FACTOR, true, false);
    public final Image      enemyTorpedoImg         = new Image(getClass().getResourceAsStream("enemyTorpedoL3.png"), 21 * SCALING_FACTOR, 21 * SCALING_FACTOR, true, false);
    public final Image      enemyBossTorpedoImg     = new Image(getClass().getResourceAsStream("enemyBossTorpedoL3.png"), 26 * SCALING_FACTOR, 26 * SCALING_FACTOR, true, false);
    public final Image      enemyBossRocketImg      = new Image(getClass().getResourceAsStream("enemyBossRocketL3.png"), 17 * SCALING_FACTOR, 42 * SCALING_FACTOR, true, false);
    public final Image      explosionImg            = new Image(getClass().getResourceAsStream("explosionL3.png"), 2048 * SCALING_FACTOR, 1792 * SCALING_FACTOR, true, false);
    public final Image      enemyBossHitImg         = new Image(getClass().getResourceAsStream("torpedoHitL3.png"), 400 * SCALING_FACTOR, 160 * SCALING_FACTOR, true, false);
    public final Image      enemyBossExplosionImg   = new Image(getClass().getResourceAsStream("enemyBossExplosionL3.png"), 800 * SCALING_FACTOR, 1400 * SCALING_FACTOR, true, false);
    public final Image      enemyRocketExplosionImg = new Image(getClass().getResourceAsStream("enemyRocketExplosionL3.png"), 512 * SCALING_FACTOR, 896 * SCALING_FACTOR, true, false);
    public final Image      levelBossImg            = new Image(getClass().getResourceAsStream("levelBossL3.png"), 300 * SCALING_FACTOR, 300 * SCALING_FACTOR, true, false);
    public final Image      levelBossTorpedoImg     = new Image(getClass().getResourceAsStream("levelBossTorpedoL3.png"), 35 * SCALING_FACTOR, 60 * SCALING_FACTOR, true, false);
    public final Image      levelBossRocketImg      = new Image(getClass().getResourceAsStream("levelBossRocketL3.png"), 14 * SCALING_FACTOR, 30 * SCALING_FACTOR, true, false);
    public final Image      levelBossBombImg        = new Image(getClass().getResourceAsStream("levelBossBombL3.png"), 30 * SCALING_FACTOR, 30 * SCALING_FACTOR, true, false);
    public final Image      levelBossExplosionImg   = new Image(getClass().getResourceAsStream("levelBossExplosionL3.png"), 2048 * SCALING_FACTOR, 768 * SCALING_FACTOR, true, false);


    @Override public Difficulty getDifficulty() { return difficulty; }

    @Override public Image getBackgroundImg() {
        return backgroundImg;
    }

    @Override public Image[] getEnemyImages() {
        return enemyImages;
    }

    @Override public Image getEnemyBossImg0() {
        return enemyBossImg0;
    }

    @Override public Image getEnemyBossImg1() {
        return enemyBossImg1;
    }

    @Override public Image getEnemyBossImg2() {
        return enemyBossImg2;
    }

    @Override public Image getEnemyBossImg3() {
        return enemyBossImg3;
    }

    @Override public Image getEnemyBossImg4() {
        return enemyBossImg4;
    }

    @Override public Image getEnemyBombImg() {
        return enemyBombImg;
    }

    @Override public Image getEnemyTorpedoImg() {
        return enemyTorpedoImg;
    }

    @Override public Image getEnemyBossTorpedoImg() {
        return enemyBossTorpedoImg;
    }

    @Override public Image getEnemyBossRocketImg() {
        return enemyBossRocketImg;
    }

    @Override public Image getExplosionImg() {
        return explosionImg;
    }

    @Override public Image getEnemyBossHitImg() {
        return enemyBossHitImg;
    }

    @Override public Image getEnemyBossExplosionImg() {
        return enemyBossExplosionImg;
    }

    @Override public Image getEnemyRocketExplosionImg() {
        return enemyRocketExplosionImg;
    }

    @Override public Image getLevelBossImg() {
        return levelBossImg;
    }

    @Override public Image getLevelBossTorpedoImg() {
        return levelBossTorpedoImg;
    }

    @Override public Image getLevelBossRocketImg() {
        return levelBossRocketImg;
    }

    @Override public Image getLevelBossBombImg() {
        return levelBossBombImg;
    }

    @Override public Image getLevelBossExplosionImg() {
        return levelBossExplosionImg;
    }
}
