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

public class Config {
    public static final double  SCALING_FACTOR             = 1.0;
    public static final double  WIDTH                      = 700;
    public static final double  HEIGHT                     = 900;
    public static final boolean PLAY_SOUND                 = true;
    public static final boolean PLAY_MUSIC                 = true;
    public static final boolean SHOW_BACKGROUND            = true;
    public static final boolean SHOW_STARS                 = true;
    public static final boolean SHOW_ENEMIES               = true;
    public static final boolean SHOW_ASTEROIDS             = true;
    public static final int     NO_OF_STARS                = SHOW_STARS ? 200 : 0;
    public static final int     NO_OF_ASTEROIDS            = SHOW_ASTEROIDS ? 10 : 0;
    public static final int     LIFES                      = 5;
    public static final int     SHIELDS                    = 10;
    public static final int     DEFLECTOR_SHIELD_TIME      = 5000;
    public static final int     MAX_NO_OF_ROCKETS          = 3;
    public static final double  TORPEDO_SPEED              = 6;
    public static final double  ROCKET_SPEED               = 4;
    public static final double  ENEMY_TORPEDO_SPEED        = 5;
    public static final double  ENEMY_BOMB_SPEED           = 3;
    public static final int     NO_OF_ENEMY_BOMBS          = 3;
    public static final double  ENEMY_BOSS_TORPEDO_SPEED   = 6;
    public static final double  ENEMY_BOSS_ROCKET_SPEED    = 4;
    public static final double  ENEMY_BOSS_SPEED           = 2;
    public static final double  LEVEL_BOSS_SPEED           = 1;
    public static final double  LEVEL_BOSS_TORPEDO_SPEED   = 6;
    public static final double  LEVEL_BOSS_BOMB_SPEED      = 4;
    public static final long    ENEMY_BOSS_ATTACK_INTERVAL = 25_000_000_000l;
    public static final long    SHIELD_UP_SPAWN_INTERVAL   = 25_000_000_000l;
    public static final long    LIFE_UP_SPAWN_INTERVAL     = 55_000_000_000l;
    public static final long    WAVE_SPAWN_INTERVAL        = 10_000_000_000l;
    public static final long    BOMB_DROP_INTERVAL         = 1_000_000_000l;
    public static final double  VELOCITY_FACTOR_X          = 1.0;
    public static final double  VELOCITY_FACTOR_Y          = 1.0;
    public static final double  VELOCITY_FACTOR_R          = 1.0;
    public static final int     NO_OF_KILLS_STAGE_1        = 50;
    public static final int     NO_OF_KILLS_STAGE_2        = 100;
    public static final int     NO_OF_ENEMIES_STAGE_1      = 5;
    public static final int     NO_OF_ENEMIES_STAGE_2      = 7;
    public static final int     NO_OF_ENEMIES_STAGE_3      = 10;
}
