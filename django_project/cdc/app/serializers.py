from rest_framework import serializers

from app.models import Author


class AuthorSerializer(serializers.HyperlinkedModelSerializer):
    class Meta:
        model = Author
        fields = ['id', 'description', 'email', 'name']
