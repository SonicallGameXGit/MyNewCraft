package org.mynewcraft.world.entity.custom;

import org.joml.Vector2d;
import org.joml.Vector3d;
import org.joml.Vector3i;
import org.mynewcraft.engine.math.physics.CubeCollider;
import org.mynewcraft.engine.time.Time;
import org.mynewcraft.world.World;
import org.mynewcraft.world.chunk.Chunk;
import org.mynewcraft.world.entity.AbstractEntity;

public class LivingEntity extends AbstractEntity {
    public double mass;
    public double speed;

    public Vector3d direction;

    public LivingEntity(CubeCollider collider, Vector3d rotation, double mass, double speed) {
        super(collider, rotation);

        this.mass = mass;
        this.speed = speed;

        direction = new Vector3d();
    }

    public void update(World world, Time time) {
        Vector3d realDirection = new Vector3d(direction).mul(speed * time.getDelta());

        collider.position.x += realDirection.x();
        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
            for(Vector3i coordinate : chunk.getCoordinates()) {
                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider)){
                    collider.position.x -= realDirection.x();
                    break;
                }
            }
        }

        collider.position.y += realDirection.y();
        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
            for(Vector3i coordinate : chunk.getCoordinates()) {
                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider)){
                    collider.position.y -= realDirection.y();
                    break;
                }
            }
        }

        collider.position.z += realDirection.z();
        for(Chunk chunk : world.getNearChunks(new Vector2d(collider.position.x(), collider.position.z()))) {
            for(Vector3i coordinate : chunk.getCoordinates()) {
                if(new CubeCollider(new Vector3d(coordinate).add(chunk.getOffset().x() * 16.0, 0.0, chunk.getOffset().y() * 16.0), new Vector3d(1.0)).checkCollision(collider)){
                    collider.position.z -= realDirection.z();
                    break;
                }
            }
        }
    }
}