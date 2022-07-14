package Package;

public enum ID {
	Null,
	
	Player,
	
	Wall,
	Powerup,
	Portal,
	BlackHole,

	//////////////////////////////////////////////////////////////////////////////////////////////////////////// ENEMIES
	Enemy,	
	
	Firefly,
	Hive,
	Firequeen,
	
	Turret,
	MagicalTurret,
	AdvancedTurret,
	Flamethrower,
	TurretArray,
	
	Golemite,
	Mimic,
	Golem,
	MegaGolem,
	
	Wisp,
	VoidCaller,
	Visionary,
	VoidTyrant,
	
	////////////////////////////////////////////////////
	/*CURRENT CHECKPOINT - EVERYTHING PRIOR IS CREATED*/
	////////////////////////////////////////////////////
	
	TheGame,		//N/A; N/A; N/A; 5000 hp; 10000 score; 20% dodge; damaged through destroying 'control orbs';
					//'control orbs' randomly spawn throughout the screen; periodically generates explosive particles;
					//periodically conjures enemies (not including bosses); periodically teleports player around screen;
					//periodic effects occur faster as health depletes. [FINAL BOSS]
	
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	Projectile,
	Explosion,
	
	Ind;
}
