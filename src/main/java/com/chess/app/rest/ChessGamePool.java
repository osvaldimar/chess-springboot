package com.chess.app.rest;

import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import com.chess.core.GameApplication;
import com.chess.core.ResponseChessboard;
import com.chess.core.client.KeyClient;
import com.chess.core.client.KeyUUIDChess;
import com.chess.core.client.PlayerMode;
import com.chess.core.client.ResponseClient;
import com.chess.core.enums.TypePlayer;
import com.chess.core.model.Difficulty;
import com.chess.core.model.Player;
import com.chess.core.service.ChessMultiplayerAI;
import com.chess.core.service.ChessMultiplayerOnline;
import com.dim.chess.ai.PlayerConnectAI;

public class ChessGamePool {

	private final Map<KeyUUIDChess, GameApplication> map = new ConcurrentHashMap<>();
	private final Queue<KeyClient> queue = new LinkedList<>();

	public GameApplication findGameApp(final String uuid, final String typePlayer) {
		try {
			if (TypePlayer.getEnum(typePlayer) == TypePlayer.W) {
				final KeyUUIDChess key = new KeyUUIDChess(UUID.fromString(uuid), null);
				final Optional<KeyUUIDChess> findFirst = this.map.keySet().stream().filter(k -> k.equals(key))
						.findFirst();
				if (findFirst.isPresent()) {
					return this.map.get(findFirst.get());
				}
			} else if (TypePlayer.getEnum(typePlayer) == TypePlayer.B) {
				final KeyUUIDChess key = new KeyUUIDChess(null, UUID.fromString(uuid));
				final Optional<KeyUUIDChess> findFirst = this.map.keySet().stream().filter(k -> k.equals(key))
						.findFirst();
				if (findFirst.isPresent()) {
					return this.map.get(findFirst.get());
				}
			}
		} catch (final NumberFormatException e) {
			return null;
		}
		return null;
	}

	public ResponseClient joinSinglePlayerOnlineChessPool() {
		final UUID singleUuid = UUID.randomUUID();
		final KeyClient keyClientW = new KeyClient(singleUuid, TypePlayer.W);
		final KeyClient keyClientB = new KeyClient(singleUuid, TypePlayer.B);
		final ChessMultiplayerOnline chessOnline = new ChessMultiplayerOnline();
		final GameApplication game = chessOnline.startChess(new Player(TypePlayer.W), new Player(TypePlayer.B));
		this.map.put(new KeyUUIDChess(keyClientW.getKey(), keyClientB.getKey()), game);
		return this.buildResponseClientStartAdequateForThePlayer(keyClientW);
	}

	public ResponseClient joinMultiPlayerOnlineChessPool() {
		final KeyClient keyClient = this.joinPoolGameMultiAddQueue();
		while (true) {
			try {
				if (this.isJoinPoolGameMulti(keyClient)) {
					break;
				}
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
		}
		return this.buildResponseClientStartAdequateForThePlayer(keyClient);
	}

	public ResponseClient joinMultiPlayerAIOnlineChessPool(final Difficulty difficultyAI) {
		final UUID singleUuid = UUID.randomUUID();
		final KeyClient keyClientW = new KeyClient(singleUuid, TypePlayer.W);
		final KeyClient keyClientB = new KeyClient(singleUuid, TypePlayer.B);
		final ChessMultiplayerAI chessOnline = new ChessMultiplayerAI();
		final PlayerMode[] players = this.randomPlayerCommonAndAI(difficultyAI);
		final GameApplication game = chessOnline.startChess(players[0], players[1]);
		this.map.put(new KeyUUIDChess(keyClientW.getKey(), keyClientB.getKey()), game);
		return this.buildResponseClientStartAdequateForThePlayer(!players[0].isAI() ? keyClientW : keyClientB);
	}

	private PlayerMode[] randomPlayerCommonAndAI(final Difficulty difficultyAI) {
		final PlayerMode[] players = { new Player(TypePlayer.W), new Player(TypePlayer.B) };
		final int r = new Random().nextInt(2);
		// players[r] = new PlayerMachineAI(players[r].getTypePlayer(),
		// difficultyAI);
		players[r] = new PlayerConnectAI(players[r].getTypePlayer(), difficultyAI);
		return players;
	}

	private ResponseClient buildResponseClientStartAdequateForThePlayer(final KeyClient keyClient) {
		return new ResponseClient.Builder().status(ResponseChessboard.StatusResponse.START.toString())
				.currentPlayer(TypePlayer.W.toString()).turn(TypePlayer.W.toString())
				.keyClientID(keyClient.getKey().toString()).keyClientType(keyClient.getType().toString()).build();
	}

	private synchronized KeyClient joinPoolGameMultiAddQueue() {
		if (this.queue.isEmpty()) {
			final KeyClient keyClient = new KeyClient(UUID.randomUUID(), TypePlayer.W);
			this.queue.add(keyClient);
			System.out.println("Thread name: " + Thread.currentThread().getName() + " - queue new W = " + keyClient);
			return keyClient;
		} else {
			final KeyClient keyClientW = this.queue.remove();
			final KeyClient keyClientB = new KeyClient(UUID.randomUUID(), TypePlayer.B);
			final ChessMultiplayerOnline chessOnline = new ChessMultiplayerOnline();
			final GameApplication game = chessOnline.startChess(new Player(TypePlayer.W), new Player(TypePlayer.B));
			this.map.put(new KeyUUIDChess(keyClientW.getKey(), keyClientB.getKey()), game);
			System.out.println("Thread name: " + Thread.currentThread().getName() + " - queue new B = " + keyClientB);
			return keyClientB;
		}
	}

	private synchronized boolean isJoinPoolGameMulti(final KeyClient keyClient) {
		final GameApplication findGameApp = this.findGameApp(keyClient.getKey().toString(),
				keyClient.getType().toString());
		return findGameApp != null;
	}

	public int getTotalChessPool() {
		return this.map.size();
	}

	public int getTotalChessQueuePending() {
		return this.queue.size();
	}
}
