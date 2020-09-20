FROM google/cloud-sdk:alpine

RUN gcloud components update beta --quiet
RUN gcloud components install bigtable --quiet

CMD ["gcloud", "beta", "emulators", "bigtable", "start"]
