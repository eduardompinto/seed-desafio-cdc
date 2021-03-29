# Create your views here.
from rest_framework import viewsets

from app.models import Author
from app.serializers import AuthorSerializer


class AuthorViewSet(viewsets.ModelViewSet):
    queryset = Author.objects.all()
    serializer_class = AuthorSerializer
