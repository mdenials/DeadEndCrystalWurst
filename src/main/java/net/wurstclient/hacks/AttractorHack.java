package net.wurstclient.hacks;

import java.util.Random;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.Category;
import net.wurstclient.SearchTags;
import net.wurstclient.WurstClient;
import net.wurstclient.events.UpdateListener;
import net.wurstclient.hack.Hack;
import net.wurstclient.hack.DontSaveState;
import net.wurstclient.settings.EnumSetting;
import net.wurstclient.settings.SliderSetting;
import net.wurstclient.settings.CheckboxSetting;
import net.wurstclient.settings.SliderSetting.ValueDisplay;
import net.wurstclient.util.BlockUtils;
import net.wurstclient.util.EntityUtils;
import net.wurstclient.util.RotationUtils;


@SearchTags({"telekinesis", "move entities"})
@DontSaveState
public final class AttractorHack extends Hack implements UpdateListener
{
private final SliderSetting range = new SliderSetting("Range", 5, 1, 512, 0.000001, ValueDisplay.DECIMAL);
private final SliderSetting fov = new SliderSetting("FOV", 360, 0, 360, 1, ValueDisplay.DEGREES);
private final SliderSetting max = new SliderSetting("Max Random", 0, -20, 20, 0.000001, ValueDisplay.DECIMAL);
private final SliderSetting min = new SliderSetting("Min Random", 0, -20, 20, 0.000001, ValueDisplay.DECIMAL);
private final SliderSetting delay = new SliderSetting("Delay", "SpeedHack delay in ticks", 0, 0, 1000, 1, ValueDisplay.INTEGER);
private final CheckboxSetting checkLOS = new CheckboxSetting("Check line of sight", "Ensures that you don't reach through blocks.\n\n" + "Slower but can help with anti-cheat plugins.", false);

private int timer;
private Entity target;

	
	public AttractorHack()
	{
		super("Attractor");
		setCategory(Category.ITEMS);
        	addSetting(range);
        	addSetting(fov);
        	addSetting(max);
        	addSetting(min);
        	addSetting(delay);
        	addSetting(checkLOS);
	}
	
	@Override
	protected void onEnable()
	{	
		// add listener
        timer = 0;
		EVENTS.add(UpdateListener.class, this);
	}
	
	@Override
	protected void onDisable()
	{
		// remove listener
		EVENTS.remove(UpdateListener.class, this);
	}
	
	@Override
	public void onUpdate()
	{
        	Stream<Entity> stream = EntityUtils.getEntities();
        	stream = stream.filter(e -> MC.player.squaredDistanceTo(e) <= range.getValueSq());

        	if(fov.getValue() < 360.0) 
        	{
        		stream = stream.filter(e -> RotationUtils.getAngleToLookVec(e.getBoundingBox().getCenter()) <= fov.getValue() / 2.0);
        	}

        	stream = stream.filter(e -> e instanceof ItemEntity || e instanceof TridentEntity || e instanceof ArrowEntity || e instanceof ExperienceOrbEntity);
        	ArrayList<Entity> entities = stream.collect(Collectors.toCollection(ArrayList::new));
        	ClientPlayNetworkHandler netHandler = MC.player.networkHandler;
        	ClientPlayerEntity player = MC.player;
        	Random random = new Random();
        	double rX = min.getValue() + (max.getValue() - min.getValue()) * random.nextDouble();
        	double rZ = min.getValue() + (max.getValue() - min.getValue()) * random.nextDouble();

        	// wait for timer
		if(timer > 0)
		{
			timer--;
			return;
		}
        	timer = delay.getValueI();

        	for(Entity entity : entities)
        	{
        		Vec3d entityPosition = new Vec3d(entity.getX(), entity.getY(), entity.getZ());
        		BlockPos blockPos = new BlockPos((int)entityPosition.x, (int)entityPosition.y, (int)entityPosition.z);

        		if(checkLOS.isChecked() && !BlockUtils.hasLineOfSight(entityPosition))
	     		{
          			continue;
			}

        		netHandler.sendPacket(new PlayerMoveC2SPacket.PositionAndOnGround(entity.getX()+rX, entity.getY(), entity.getZ()+rZ, entity.isOnGround()));
        	}
  	}
}
