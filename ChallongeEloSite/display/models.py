from django.db import models

class aliases(models.Model):
	name = models.CharField(max_length=255)
	alias = models.CharField(max_length=255, primary_key=True, db_column="alias")

	def __str__(self):
		return self.name + ":" + self.alias

	class Meta:
		db_table="aliases"

class tournaments(models.Model):
	tourney_id = models.AutoField(primary_key=True, db_column="tourney_id")
	date_started = models.DateTimeField()
	name = models.CharField(max_length=255)
	link = models.CharField(max_length=255)

	def __str__(self):
		return self.name + ", occurred at: " + str(self.date_started) + " found online at: " + self.link

	def getDate(self):
		return str(self.date_started)[:10]

	class Meta:
		db_table="tournaments"
		ordering=['date_started']

class players(models.Model):
	player_id = models.AutoField(primary_key=True, db_column="player_id")
	elo = models.IntegerField()
	name = models.CharField(max_length=255, unique=True)

	def __str__(self):
		return "Player " + self.name + " has an elo of " + str(self.elo)

	#return ever opponent this player has ever defeated
	def getWonOpponents(self):
		toReturn = []
		for match in self.won_matches.all():
			if (not(match.loser_id in toReturn)):
				toReturn.append(match.loser_id)

		return toReturn

	#get every opponent ever lost to
	def getLostOpponents(self):
		toReturn = []
		for match in self.lost_matches.all():
			if (not(match.winner_id in toReturn)):
				toReturn.append(match.winner_id)

		return toReturn

	def getOpponents(self):
		beat = self.getWonOpponents()
		lost = self.getLostOpponents()

		toReturn = []

		for id in beat:
			toReturn.append(id)

		for id in lost:
			if (not(id in toReturn)):
				toReturn.append(id)

		toReturn.sort(key=lambda x: x.name)
		return toReturn

	def getMatchesLostTo(self, oppId):
		return self.lost_matches.filter(winner_id=oppId).order_by('tourney_id__date_started')

	def getMatchesWonAgainst(self, oppId):
		return self.won_matches.filter(loser_id=oppId).order_by('tourney_id__date_started')

	class Meta:
		db_table="players"

class matches(models.Model):
	match_id = models.AutoField(primary_key=True, db_column="match_id")
	winner_id = models.ForeignKey(players, on_delete=models.CASCADE, db_column="winner_id", related_name="won_matches")
	loser_id = models.ForeignKey(players, on_delete=models.CASCADE, db_column="loser_id", related_name="lost_matches")
	winner_score = models.IntegerField()
	loser_score = models.IntegerField()
	tourney_id = models.ForeignKey(tournaments, on_delete=models.CASCADE, db_column="tourney_id")

	def __str__(self):
		return str(self.winner_id) + " " + str(self.winner_score) + ":" + str(self.loser_score) + " " + str(self.loser_id) + " at touranment " + str(self.tourney_id)

	class Meta:
		db_table="matches"

class placings(models.Model):
	placing_id = models.AutoField(primary_key=True, db_column="placing_id")
	player_id = models.ForeignKey(players, on_delete=models.CASCADE, db_column="player_id")
	tourney_id = models.ForeignKey(tournaments, on_delete=models.CASCADE, db_column="tourney_id")
	placing = models.IntegerField()

	def __str__(self):
		return str(self.player_id) + " placed " + str(self.placing) + " at " + str(self.tourney_id)

	class Meta:
		db_table="placings"
		ordering=['tourney_id__date_started']
