from django import template

register = template.Library()

@register.simple_tag
def getMatchesWonAgainstTag(obj, oppId):
    return obj.getMatchesWonAgainst(oppId)

@register.simple_tag
def getMatchesLostAgainstTag(obj, oppId):
    return obj.getMatchesLostTo(oppId)

@register.simple_tag
def getSetAndGameCount(matchesWon, matchesLost):
    #this takes a set of matches and returns the set and game count as strings
    #game count is return_value[0], set count is [1]
    gamesWon = 0
    gamesLost = 0
    setsWon = 0
    setsLost = 0

    for match in matchesWon:
        setsWon += 1
        gamesWon += match.winner_score
        gamesLost += match.loser_score

    for match in matchesLost:
        setsLost += 1
        gamesWon += match.loser_score
        gamesLost += match.winner_score

    games = str(gamesWon) + "-" + str(gamesLost)
    sets = str(setsWon) + "-" + str(setsLost)

    return [games, sets]
